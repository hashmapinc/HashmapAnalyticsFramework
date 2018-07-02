package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.actors.message.query.ExecuteQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CancelJob;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CreateJob;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataQueryActor extends AbstractActor {

    private final MetadataConfig metadataConfig;
    private MetadataQuery metadataQuery;
    private ActorRef scheduler;

    private MetadataQueryActor(MetadataConfig metadataConfig, MetadataQuery metadataQuery) {
        this.metadataConfig = metadataConfig;
        this.metadataQuery = metadataQuery;
    }

    static public Props props(MetadataConfig metadataConfig, MetadataQuery metadataQuery) {
        return Props.create(MetadataQueryActor.class, () -> new MetadataQueryActor(metadataConfig, metadataQuery))
                    .withDispatcher(getQueryDispatcher());
    }

    private  void processMessage(Object message) throws Exception {
        if (message instanceof ExecuteQueryMsg) {
            log.info("Processing ExecuteQueryMsg");
            executeQuery();
        } else if (message instanceof QueryMessage) {
            if (((QueryMessage)message).getMessageType() == MessageType.UPDATE) {
                metadataQuery = ((QueryMessage) message).getMetadataQuery();
            } else if (((QueryMessage) message).getMessageType() == MessageType.DELETE) {
                scheduler.tell(new CancelJob(metadataQuery.getId()), ActorRef.noSender());
                context().stop(self());
            }
        }
    }

    private void executeQuery() {
        //TODO:it will be called by scheduler and this will perform ingestion
        log.info("ExecuteQuery : MetaQuery{}", metadataQuery);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        scheduler = context().actorFor(ManagerActorService.getSchedulerPath());
        if (scheduler != null) {
            scheduler.tell(new CreateJob(metadataQuery, self(), new ExecuteQueryMsg()), ActorRef.noSender());
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        scheduler.tell(new CancelJob(metadataQuery.getId()), ActorRef.noSender());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ExecuteQueryMsg.class, this::processMessage)
                .match(QueryMessage.class, this::processMessage)
                .matchAny(o -> log.info("received unknown message [{}]", o.getClass().getName()))
                .build();
    }

    private static String getQueryDispatcher() {
        return ManagerActorService.QUERY_DISPATCHER;
    }
}
