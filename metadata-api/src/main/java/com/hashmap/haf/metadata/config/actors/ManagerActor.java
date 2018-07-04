package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ManagerActor extends AbstractLoggingActor {

    private final Map<String, ActorRef> ownerIdToActor = new HashMap<>();
    private final Map<ActorRef, String> actorToOwnerId = new HashMap<>();

    public static Props props() {
        return Props.create(ManagerActor.class);
    }

    private SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create(3, TimeUnit.SECONDS),
                    DeciderBuilder
                            .match(Exception.class, e -> {
                                log.info("Exception {}", e.getMessage());
                                return akka.actor.SupervisorStrategy.resume();
                            })
                            .matchAny(o -> akka.actor.SupervisorStrategy.escalate())
                            .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    private void processMessage(AbstractMessage message) {
        String ownerId = message.getMetadataConfig().getOwnerId();

        ActorRef ownerActor = ownerIdToActor.get(ownerId);
        if (ownerActor != null) {
            log.debug("Found metadata config owner group actors for OwnerId : {}", ownerId);
            ownerActor.tell(message, ActorRef.noSender());
        } else {
            log.debug("Creating metadata config owner group actors for OwnerId : {}", ownerId);
            createMetaDataConfigOwnerActor(message, ownerId);
        }
    }

    private void createMetaDataConfigOwnerActor(Object message ,String ownerId) {
        ActorRef ownerActor = getContext().actorOf(MetadataConfigOwnerActor.props(ownerId), ownerId);
        getContext().watch(ownerActor);
        ownerIdToActor.put(ownerId, ownerActor);
        actorToOwnerId.put(ownerActor, ownerId);
        ownerActor.tell(message, ActorRef.noSender());
    }

    private void onTerminated(Terminated t) {
        ActorRef ownerActor = t.getActor();
        String ownerId = actorToOwnerId.get(ownerActor);
        log.info("Owner actors for {} has been terminated", ownerId);
        actorToOwnerId.remove(ownerActor);
        ownerIdToActor.remove(ownerId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MetadataMessage.class, this::processMessage)
                .match(QueryMessage.class, this::processMessage)
                .match(RunIngestionMsg.class, this::processMessage)
                .match(Terminated.class, this::onTerminated)
                .matchAny(o -> log.info("received unknown message [{}]", o.getClass().getName()))
                .build();
    }
}
