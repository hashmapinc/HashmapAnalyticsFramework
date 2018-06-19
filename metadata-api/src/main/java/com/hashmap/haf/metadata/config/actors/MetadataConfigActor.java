package com.hashmap.haf.metadata.config.actors;

import akka.actor.*;
import com.hashmap.haf.metadata.config.actors.message.*;
import com.hashmap.haf.metadata.config.actors.message.metadata.DeleteMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.metadata.UpdateMetadataConfigMsg;
import com.hashmap.haf.metadata.config.actors.message.query.*;
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

    private  void processMetadataConfigMsg(Object message) {
        if (message instanceof UpdateMetadataConfigMsg) {
            log.debug("Updating metadataConfig actors for {}", metadataConfig.getId());
            metadataConfig = ((UpdateMetadataConfigMsg) message).getMetadataConfig();
        } else if (message instanceof DeleteMetadataConfigMsg) {
            log.debug("Deleting metadataConfig actors for {}",  metadataConfig.getId());
            schedulerExtension.cancelJob("queryScheduler" + metadataConfig.getId());
            context().stop(self());
        }
    }

    private void processQueryMsg(Object message) {
        if (message instanceof CreateQueryMsg) {
            log.debug("Message type CreateQueryMsg");
            queries.add(((CreateQueryMsg) message).getQuery());
            executeQuery(queries);
        } else if (message instanceof UpdateQueryMsg) {
            //TODO : Will be implemented after query support according to QueryId
        } else if (message instanceof DeleteQueryMsg) {
            //TODO : Will be implemented after query support according to QueryId
        } else if (message instanceof TestConnectionMsg) {
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
            queryActor.tell(new StartQueryMsg(), ActorRef.noSender());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateMetadataConfigMsg.class, this::processMetadataConfigMsg)
                .match(DeleteMetadataConfigMsg.class, this::processMetadataConfigMsg)
                .match(CreateQueryMsg.class, this::processQueryMsg)
                .match(DeleteQueryMsg.class, this::processQueryMsg)
                .match(UpdateQueryMsg.class, this::processQueryMsg)
                .match(ScheduleQueryMsg.class, this::processQueryMsg)
                .match(TestConnectionMsg.class, this::processQueryMsg)
                .match(RunIngestionMsg.class, this::processQueryMsg)
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
