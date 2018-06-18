package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.message.AbstractMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.TestConnectionMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.CreateMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.DeleteMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.UpdateMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.query.CreateQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.DeleteQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.UpdateQueryMsg;
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

    private void processMetadataConfigMsg(Object message) {
        MetadataConfig metadataConfig = ((AbstractMetadataConfigMsg)message).getMetadataConfig();
        MetadataConfigId metadataConfigId = metadataConfig.getId();
        String ownerId = metadataConfig.getOwnerId();

        if (this.ownerId.equals(ownerId)) {
            ActorRef metadataConfigActor = metadataConfigIdToActor.get(metadataConfigId);
            if (metadataConfigActor != null) {
                log.info("Found metadataConfig actors for {}", metadataConfigId);
                if (!(message instanceof CreateMetadataConfigMsg)) {
                    metadataConfigActor.tell(message, ActorRef.noSender());
                }
            } else {
                if (message instanceof CreateMetadataConfigMsg) {
                    log.info("Creating metadataConfig actors for {}", metadataConfigId);
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
    }

    //TODO:add query handling needs to be done after query support
    private  void processQueryMsg(Object message) {
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateMetadataConfigMsg.class, this::processMetadataConfigMsg)
                .match(DeleteMetadataConfigMsg.class, this::processMetadataConfigMsg)
                .match(UpdateMetadataConfigMsg.class, this::processMetadataConfigMsg )
                .match(CreateQueryMsg.class, this::processQueryMsg)
                .match(DeleteQueryMsg.class, this::processQueryMsg)
                .match(UpdateQueryMsg.class, this::processQueryMsg)
                .match(Terminated.class, this::onTerminated)
                .match(TestConnectionMsg.class, this::processQueryMsg)
                .match(RunIngestionMsg.class, this::processQueryMsg)
                .build();
    }
}
