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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataQueryActor extends AbstractActor {

    private final MetadataConfig metadataConfig;
    private String query;
    private ActorRef scheduler;

    private MetadataQueryActor(MetadataConfig metadataConfig, String query) {
        this.metadataConfig = metadataConfig;
        this.query = query;
    }

    static public Props props(MetadataConfig metadataConfig, String query) {
        return Props.create(MetadataQueryActor.class, () -> new MetadataQueryActor(metadataConfig, query))
                    .withDispatcher(getQueryDispatcher());
    }

    private  void processMessage(Object message) throws Exception {
        if (message instanceof ExecuteQueryMsg) {
            log.info("MetadataQueryActor : MetadataConfig : {}", metadataConfig.toString());
            log.info("MetadataQueryActor : Query : {}", query);
            executeQuery(query);
        } else if (message instanceof QueryMessage) {
            if (((QueryMessage)message).getMessageType() == MessageType.UPDATE) {
                query = ((QueryMessage) message).getQuery();
            } else if (((QueryMessage) message).getMessageType() == MessageType.DELETE) {
                log.info("QUery Delete : ");
                scheduler.tell(new CancelJob(query), ActorRef.noSender());
                context().stop(self());
            }
        }
    }

    private void executeQuery(String query) {
        //TODO:it will be called by scheduler and this will perform ingestion
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("In Prestart....");
        scheduler = context().actorFor(ManagerActorService.getSchedulerPath());
        if (scheduler != null) {
            log.info("Not NUll Scheduler");
            scheduler.tell(new CreateJob(query, metadataConfig.getTriggerType(), metadataConfig.getTriggerSchedule(), self(), new ExecuteQueryMsg()), ActorRef.noSender());
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("Calling Post Stop");
        scheduler.tell(new CancelJob(query), ActorRef.noSender());
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
