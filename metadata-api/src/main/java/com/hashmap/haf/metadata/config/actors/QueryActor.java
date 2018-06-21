package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hashmap.haf.metadata.config.actors.message.query.ExecuteQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryActor extends AbstractActor {

    private final MetadataConfig metadataConfig;
    private final String query;

    private QueryActor(MetadataConfig metadataConfig, String query) {
        this.metadataConfig = metadataConfig;
        this.query = query;
    }

    static public Props props(MetadataConfig metadataConfig, String query) {
        return Props.create(QueryActor.class, () -> new QueryActor(metadataConfig, query));
    }

    private  void processMessage(Object message) {
        if (message instanceof ExecuteQueryMsg) {
            log.debug("QueryActor : MetadataConfig : {}", metadataConfig.toString());
            log.debug("QueryActor : MetadataQuery : {}", query);
            context().stop(self());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ExecuteQueryMsg.class, this::processMessage)
                .build();
    }
}
