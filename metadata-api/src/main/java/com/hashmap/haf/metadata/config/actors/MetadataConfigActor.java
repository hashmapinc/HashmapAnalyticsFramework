package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.hashmap.haf.metadata.config.actors.message.*;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.TestConnectionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

@Slf4j
public class MetadataConfigActor extends AbstractActor {

    private MetadataConfig metadataConfig;
    final Map<Integer, ActorRef> metadataQueryIdToActor = new HashMap<>();
    final Map<ActorRef, Integer> actorToMetadataQueryId = new HashMap<>();

    static public Props props() {
        return Props.create(MetadataConfigActor.class).withDispatcher(getMetadataDispatcher());
    }

    private SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create(3, TimeUnit.SECONDS),
            DeciderBuilder.match(Exception.class, e -> {
                log.info("Exception {}", e.getMessage());
                return akka.actor.SupervisorStrategy.restart();
            })
                    .matchAny(o -> akka.actor.SupervisorStrategy.escalate())
                    .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    private  void processMetadataConfigMsg(MetadataMessage message) {
        metadataConfig = message.getMetadataConfig();
        if (message.getMessageType() == MessageType.CREATE) {
            log.debug("Message Create metadataConfig actor");
        } else if (message.getMessageType() == MessageType.UPDATE) {
            log.debug("Updating metadataConfig actors for {}", metadataConfig.getId());
            metadataConfig = message.getMetadataConfig();
        } else if (message.getMessageType() == MessageType.DELETE) {
            log.debug("Deleting metadataConfig actors for {}",  metadataConfig.getId());
            context().stop(self());
        }
    }

    private void processQueryMsg(QueryMessage message) {
        metadataConfig = message.getMetadataConfig();
        String query = message.getQuery();
        ActorRef metadataQueryActor = metadataQueryIdToActor.get(query.hashCode());
        if(metadataQueryActor != null) {
            log.debug("Found metadataQuery actors for MetadataQueryId : {}", query.hashCode());
            metadataQueryActor.tell(message, ActorRef.noSender());
        } else {
            log.debug("Creating metadataQuery actors for MetadataQueryId : {}", query.hashCode());
            createMetadataQueryActor(message, metadataConfig);
        }
    }

    private void createMetadataQueryActor(QueryMessage message, MetadataConfig metadataConfig) {
        ActorRef metadataQueryActor = getContext().actorOf(MetadataQueryActor.props(metadataConfig, message.getQuery()/*, schedulerExtension*/), Integer.toString(message.getQuery().hashCode()));
        getContext().watch(metadataQueryActor);
        metadataQueryIdToActor.put(message.getQuery().hashCode(),metadataQueryActor);
        actorToMetadataQueryId.put(metadataQueryActor,message.getQuery().hashCode());
        metadataQueryActor.tell(message, ActorRef.noSender());
    }

    private void processMessage(Object message) {
        if (message instanceof TestConnectionMsg) {
            //TODO : Will be implemented after query support
        } else if (message instanceof RunIngestionMsg) {
            //TODO : Will be implemented after query support
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef metadataQueryActor = t.getActor();
        Integer metadataQueryId = actorToMetadataQueryId.get(metadataQueryActor);
        log.info("MetadataQuery actors for {} has been terminated", metadataQueryId);
        actorToMetadataQueryId.remove(metadataQueryActor);
        metadataQueryIdToActor.remove(metadataQueryId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MetadataMessage.class, this::processMetadataConfigMsg)
                .match(QueryMessage.class, this::processQueryMsg)
                .match(TestConnectionMsg.class, this::processMessage)
                .match(RunIngestionMsg.class, this::processMessage)
                .matchAny(o -> log.info("received unknown message [{}]", o.getClass().getName()))
                .build();
    }

    private static String getMetadataDispatcher() {
        return ManagerActorService.METADATA_DISPATCHER;
    }

}
