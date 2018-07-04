package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

@Slf4j
public class MetadataConfigOwnerActor extends AbstractLoggingActor {

    private final String ownerId;

    private ActorRef shardingMetadataConfig = null;

    private MetadataConfigOwnerActor(String ownerId) {
        this.ownerId = ownerId;
    }

    static Props props(String ownerId) {
        return Props.create(MetadataConfigOwnerActor.class, () -> new MetadataConfigOwnerActor(ownerId)).withDispatcher(getOwnerDispatcher());
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

    private void processMessage(Object message) {
        MetadataConfig metadataConfig = ((AbstractMessage)message).getMetadataConfig();
        String ownerId = metadataConfig.getOwnerId();

        if (this.ownerId.equals(ownerId)) {
            if (shardingMetadataConfig == null) {
                shardingMetadataConfig = getContext().actorOf(ShardingMetadataConfig.props());
            }
            shardingMetadataConfig.tell(message, ActorRef.noSender());
        } else {
            log.warn(
                    "Ignoring message request for {}. This actors is responsible for {}.",
                    ownerId, this.ownerId
            );
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef metadataConfigActor = t.getActor();
//        MetadataConfigId metadataConfigId = actorToMetadataConfigId.get(metadataConfigActor);
//        log.info("MetadataConfig actors for {} has been terminated", metadataConfigId);
//        actorToMetadataConfigId.remove(metadataConfigActor);
//        metadataConfigIdToActor.remove(metadataConfigId);
//        if (actorToMetadataConfigId.size() == 0) {
//            context().stop(self());
//        }
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

    private static String getOwnerDispatcher() {
        return ManagerActorService.OWNER_DISPATCHER;
    }
}
