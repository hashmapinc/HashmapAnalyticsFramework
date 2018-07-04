package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Option;
import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ShardingMetadataConfig extends AbstractActor {

    private final ActorRef metadataConfig;

    private static int numberOfShards;

    private ShardingMetadataConfig() {
        ActorSystem system = context().system();
        Option<String> roleOption = Option.none();
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        numberOfShards = Integer.parseInt(context().system().settings().config().getString("akka.numberOfShards"));

        metadataConfig = ClusterSharding.get(system).start(
                "MetadataConfigActor",
                Props.create(MetadataConfigActor.class),
                settings,
                messageExtractor
        );
    }

    static Props props() {
        return Props.create(ShardingMetadataConfig.class);
    }

    private static ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {
        @Override
        public String shardId(Object message) {
            return String.valueOf(((AbstractMessage)message).getMetadataConfig().getId().hashCode() % numberOfShards);
        }

        @Override
        public String entityId(Object message) {
            return ((AbstractMessage)message).getMetadataConfig().getId().toString();
        }

        @Override
        public Object entityMessage(Object message) {
            return message;
        }
    };

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MetadataMessage.class, msg -> metadataConfig.tell(msg, ActorRef.noSender()))
                .match(QueryMessage.class, msg -> metadataConfig.tell(msg, ActorRef.noSender()))
                .match(RunIngestionMsg.class, msg -> metadataConfig.tell(msg, ActorRef.noSender()))
                .matchAny(o -> log.warn("received unknown message [{}]", o.getClass().getName()))
                .build();
    }
}
