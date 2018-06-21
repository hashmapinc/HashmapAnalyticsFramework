package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.message.*;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.TestConnectionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MetadataConfigOwnerActor extends AbstractLoggingActor {

    String ownerId;

    final Map<MetadataConfigId, ActorRef> metadataConfigIdToActor = new HashMap<>();
    final Map<ActorRef, MetadataConfigId> actorToMetadataConfigId = new HashMap<>();
    private final QuartzSchedulerExtension schedulerExtension;

    private MetadataConfigOwnerActor(String ownerId, QuartzSchedulerExtension schedulerExtension) {
        this.ownerId = ownerId;
        this.schedulerExtension = schedulerExtension;
    }

    static public Props props(String ownerId, QuartzSchedulerExtension schedulerExtension) {
        return Props.create(MetadataConfigOwnerActor.class, () -> new MetadataConfigOwnerActor(ownerId, schedulerExtension));
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return SupervisionStrategy.getStrategy();
    }

    private void processMetadataConfigMsg(MetadataMessage message) {
        MetadataConfig metadataConfig = message.getMetadataConfig();
        MetadataConfigId metadataConfigId = metadataConfig.getId();
        String ownerId = metadataConfig.getOwnerId();

        if (this.ownerId.equals(ownerId)) {
            ActorRef metadataConfigActor = metadataConfigIdToActor.get(metadataConfigId);
            if (metadataConfigActor != null) {
                log.debug("Found metadataConfig actors for MetadataConfigId : {}", metadataConfigId);
                if (!(message.getMessageType() == MessageType.CREATE)) {
                    metadataConfigActor.tell(message, ActorRef.noSender());
                }
            } else {
                if (message.getMessageType() == MessageType.CREATE) {
                    log.debug("Creating metadataConfig actors for MetadataConfigId : {}", metadataConfigId);
                    metadataConfigActor = getContext().actorOf(MetadataConfigActor.props(metadataConfig, schedulerExtension), "metadataConfig-" + metadataConfigId);
                    getContext().watch(metadataConfigActor);
                    actorToMetadataConfigId.put(metadataConfigActor, metadataConfigId);
                    metadataConfigIdToActor.put(metadataConfigId, metadataConfigActor);
//                    metadataConfigActor.tell(message, ActorRef.noSender());
                }
            }
        } else {
            log.warn(
                    "Ignoring message request for {}. This actors is responsible for {}.",
                    ownerId, this.ownerId
            );
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef metadataConfigActor = t.getActor();
        MetadataConfigId metadataConfigId = actorToMetadataConfigId.get(metadataConfigActor);
        log.info("MetadataConfig actors for {} has been terminated", metadataConfigId);
        actorToMetadataConfigId.remove(metadataConfigActor);
        metadataConfigIdToActor.remove(metadataConfigId);
        if (actorToMetadataConfigId.size() == 0) {
            context().stop(self());
        }
    }

    private void processQueryMsg(QueryMessage message){
        MetadataConfig metadataConfig = message.getMetadataConfig();
        MetadataConfigId metadataConfigId = metadataConfig.getId();
        String ownerId = metadataConfig.getOwnerId();

        if (this.ownerId.equals(ownerId)) {
            ActorRef ref = metadataConfigIdToActor.get(metadataConfigId);
            if (ref != null) {
                log.debug("Found metadataConfig group actors for MetadataConfigId : {}", metadataConfigId);
                ref.tell(message, ActorRef.noSender());
            }
        }else {
            log.warn(
                    "Ignoring MetadataQuery message request for {}. This actors is responsible for {}.",
                    ownerId, this.ownerId
            );
        }
    }

    private void processMessage(Object message) {
        //TODO: Needs to be implemented for runIngestion and testConnection
        if (message instanceof TestConnectionMsg) {
            //TODO : Will be implemented after query support
        } else if (message instanceof RunIngestionMsg) {
            //TODO : Will be implemented after query support
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MetadataMessage.class, this::processMetadataConfigMsg)
                .match(QueryMessage.class, this::processQueryMsg)
                .match(Terminated.class, this::onTerminated)
                .match(TestConnectionMsg.class, this::processMessage)
                .match(RunIngestionMsg.class, this::processMessage)
                .build();
    }
}
