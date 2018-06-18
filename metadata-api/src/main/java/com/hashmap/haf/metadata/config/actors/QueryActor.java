package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hashmap.haf.metadata.config.actors.message.query.StartQueryMsg;
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
        if (message instanceof StartQueryMsg) {
            log.info("QueryActor : MetadataConfig : {}", metadataConfig.toString());
            log.info("QueryActor : Query : {}", query);
            context().stop(self());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartQueryMsg.class, this::processMessage)
                .build();
    }
}
