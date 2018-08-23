package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.actors.message.query.ExecuteQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CancelJob;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CreateJob;
import com.hashmap.haf.metadata.config.actors.service.ActorSystemContext;
import com.hashmap.haf.metadata.config.actors.service.ManagerActorService;
import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import com.hashmap.haf.metadata.config.model.data.resource.rest.RestResource;
import com.hashmap.haf.metadata.config.model.query.MetadataQuery;
import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import com.hashmap.haf.metadata.config.requests.IngestMetadataRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MetadataQueryActor extends AbstractActor {

    private final MetadataConfig metadataConfig;
    private MetadataQuery metadataQuery;
    private ActorRef scheduler;
    private final ActorSystemContext actorSystemContext;

    private MetadataQueryActor(ActorSystemContext actorSystemContext, MetadataConfig metadataConfig, MetadataQuery metadataQuery) {
        this.actorSystemContext = actorSystemContext;
        this.metadataConfig = metadataConfig;
        this.metadataQuery = metadataQuery;
    }

    static public Props props(ActorSystemContext actorSystemContext, MetadataConfig metadataConfig, MetadataQuery metadataQuery) {
        return Props.create(MetadataQueryActor.class, () -> new MetadataQueryActor(actorSystemContext, metadataConfig, metadataQuery))
                    .withDispatcher(getQueryDispatcher());
    }

    private  void processMessage(Object message) throws Exception {
        if (message instanceof ExecuteQueryMsg) {
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

    private void executeQuery() throws Exception {
        DataResource source = metadataConfig.getSource();
        DataResource sink = metadataConfig.getSink();

        if(sink instanceof RestResource){
            ((RestResource) sink).setRestTemplate(actorSystemContext.getOauth2RestTemplate());
        }

        Map data = source.pull(metadataQuery.getQueryStmt());
        final IngestMetadataRequest payload = IngestMetadataRequest.builder()
                .configId(metadataConfig.getId())
                .configName(metadataConfig.getName())
                .ownerId(metadataConfig.getOwnerId())
                .data(data)
                .build();
        sink.push(payload);
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
                .matchAny(o -> log.warn("received unknown message [{}]", o.getClass().getName()))
                .build();
    }

    private static String getQueryDispatcher() {
        return ManagerActorService.QUERY_DISPATCHER;
    }
}
