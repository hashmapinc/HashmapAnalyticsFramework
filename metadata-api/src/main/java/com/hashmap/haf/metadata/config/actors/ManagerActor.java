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

    private void processMetadataConfigMsg(Object message) {
        String ownerId = ((AbstractMetadataConfigMsg)message).getMetadataConfig().getOwnerId();

        ActorRef ref = ownerIdToActor.get(ownerId);
        if (ref != null) {
            log.info("Found metadata config owner group actors for {}", ownerId);
            ref.tell(message, ActorRef.noSender());
        } else {
            if(message instanceof CreateMetadataConfigMsg) {
                log.info("Creating metadata config owner group actors for {}", ownerId);
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

    private void processQueryMsg(Object message){
        //TODO: Process all query Message
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
                .match(TestConnectionMsg.class, this::processQueryMsg)
                .match(RunIngestionMsg.class, this::processQueryMsg)
                .match(Terminated.class, this::onTerminated)
                .build();
    }
}
