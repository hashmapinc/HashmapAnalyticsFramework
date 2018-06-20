package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.message.*;
import com.hashmap.haf.metadata.config.actors.message.metadata.MetadataMessage;
import com.hashmap.haf.metadata.config.actors.message.metadata.RunIngestionMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.TestConnectionMsg;
import com.hashmap.haf.metadata.config.actors.message.query.ExecuteQueryMsg;
import com.hashmap.haf.metadata.config.actors.message.query.QueryMessage;
import com.hashmap.haf.metadata.config.actors.message.query.ScheduleQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import lombok.extern.slf4j.Slf4j;
import scala.Option;
import scala.Some;

import java.util.*;

@Slf4j
public class MetadataConfigActor extends AbstractActor {

    private MetadataConfig metadataConfig;
    Set<String> queries = new HashSet<>();
    private final QuartzSchedulerExtension schedulerExtension;

    private MetadataConfigActor(MetadataConfig metadataConfig, QuartzSchedulerExtension schedulerExtension) {
        this.metadataConfig = metadataConfig;
        this.schedulerExtension = schedulerExtension;
    }

    static public Props props(MetadataConfig metadataConfig, QuartzSchedulerExtension schedulerExtension) {
        return Props.create(MetadataConfigActor.class, () -> new MetadataConfigActor(metadataConfig, schedulerExtension));
    }

    private  void processMetadataConfigMsg(MetadataMessage message) {
        if (message.getMessageType() == MessageType.UPDATE) {
            log.debug("Updating metadataConfig actors for {}", metadataConfig.getId());
            metadataConfig = message.getMetadataConfig();
        } else if (message.getMessageType() == MessageType.DELETE) {
            log.debug("Deleting metadataConfig actors for {}",  metadataConfig.getId());
            schedulerExtension.cancelJob("queryScheduler" + metadataConfig.getId());
            context().stop(self());
        }
    }

    private void processQueryMsg(QueryMessage message) {
        if (message.getMessageType() == MessageType.CREATE) {
            log.debug("Message type CreateQueryMsg");
            queries.add(message.getQuery());
            executeQuery(queries);
        } else if (message.getMessageType() == MessageType.UPDATE) {
            //TODO : Will be implemented after query support according to QueryId
        } else if (message.getMessageType() == MessageType.DELETE) {
            //TODO : Will be implemented after query support according to QueryId
        }
    }
    
    private void processMessage(Object message) {
        if (message instanceof TestConnectionMsg) {
            //TODO : Will be implemented after query support
        } else if (message instanceof RunIngestionMsg) {
            //TODO : Will be implemented after query support
        } else if (message instanceof ScheduleQueryMsg) {
            log.debug("Has Query : {}, MetadataConfigId : {}", !queries.isEmpty(), metadataConfig.getId());
            executeQuery(queries);
        }
    }

    private void executeQuery(Set<String> queries) {
        for(String q : queries) {
            ActorRef queryActor;
            queryActor = getContext().actorOf(QueryActor.props(metadataConfig, q), "query-" + q.hashCode());
            queryActor.tell(new ExecuteQueryMsg(), ActorRef.noSender());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MetadataMessage.class, this::processMetadataConfigMsg)
                .match(QueryMessage.class, this::processQueryMsg)
                .match(ScheduleQueryMsg.class, this::processMessage)
                .match(TestConnectionMsg.class, this::processMessage)
                .match(RunIngestionMsg.class, this::processMessage)
                .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        Option<String> description = new Some("description");
        Option<String> cronCalender = Option.empty();
        TimeZone timeZone = TimeZone.getTimeZone("UTC");

        if (metadataConfig.getTriggerType() == TriggerType.CRON) {
            schedulerExtension.createSchedule("queryScheduler" + metadataConfig.getId(), description, metadataConfig.getTriggerSchedule(), cronCalender, timeZone);
            schedulerExtension.schedule("queryScheduler" + metadataConfig.getId(), self(), new ScheduleQueryMsg(queries));
        }
    }
}
