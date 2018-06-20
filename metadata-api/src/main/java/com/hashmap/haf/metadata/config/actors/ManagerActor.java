package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.message.*;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.TestConnectionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ManagerActor extends AbstractLoggingActor {

    final Map<String, ActorRef> ownerIdToActor = new HashMap<>();
    final Map<ActorRef, String> actorToOwnerId = new HashMap<>();
    QuartzSchedulerExtension schedulerExtension = new QuartzSchedulerExtension((ExtendedActorSystem) context().system());

    public static Props props() {
        return Props.create(ManagerActor.class);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return SupervisionStrategy.getStrategy();
    }

    private void processMetadataConfigMsg(MetadataMessage message) {
        String ownerId = message.getMetadataConfig().getOwnerId();

        ActorRef ref = ownerIdToActor.get(ownerId);
        if (ref != null) {
            log.debug("Found metadata config owner group actors for OwnerId : {}", ownerId);
            ref.tell(message, ActorRef.noSender());
        } else {
            if(message.getMessageType() == MessageType.CREATE) {
                log.debug("Creating metadata config owner group actors for OwnerId : {}", ownerId);
                createMetaDataConfigOwnerActor(message, ownerId);
            }
        }
    }

    private void createMetaDataConfigOwnerActor(Object message ,String ownerId) {
        ActorRef ownerActor = getContext().actorOf(MetadataConfigOwnerActor.props(ownerId, schedulerExtension), "ownerId-" + ownerId);
        getContext().watch(ownerActor);
        ownerActor.tell(message, ActorRef.noSender());
        ownerIdToActor.put(ownerId, ownerActor);
        actorToOwnerId.put(ownerActor, ownerId);
    }

    private void onTerminated(Terminated t) {
        ActorRef ownerActor = t.getActor();
        String ownerId = actorToOwnerId.get(ownerActor);
        log.info("Owner actors for {} has been terminated", ownerId);
        actorToOwnerId.remove(ownerActor);
        ownerIdToActor.remove(ownerId);
    }

    private void processQueryMsg(QueryMessage message){
        String ownerId = message.getMetadataConfig().getOwnerId();
        ActorRef ref = ownerIdToActor.get(ownerId);
        if (ref != null) {
            log.debug("Found metadata config owner group actors for OwnerId : {}", ownerId);
            ref.tell(message, ActorRef.noSender());
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
                .match(TestConnectionMsg.class, this::processMessage)
                .match(RunIngestionMsg.class, this::processMessage)
                .match(Terminated.class, this::onTerminated)
                .build();
    }
}
