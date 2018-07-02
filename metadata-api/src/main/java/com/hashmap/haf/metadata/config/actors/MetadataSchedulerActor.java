package com.hashmap.haf.metadata.config.actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ExtendedActorSystem;
import akka.actor.Props;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CancelJob;
import com.hashmap.haf.metadata.config.actors.message.scheduler.CreateJob;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension;
import lombok.extern.slf4j.Slf4j;
import scala.Option;
import scala.Some;

import java.util.TimeZone;

@Slf4j
public class MetadataSchedulerActor extends AbstractLoggingActor {

    QuartzSchedulerExtension schedulerExtension = new QuartzSchedulerExtension((ExtendedActorSystem) context().system());
    Option<String> description = new Some("description");
    Option<String> cronCalender = Option.empty();
    TimeZone timeZone = TimeZone.getTimeZone(context().system().settings().config().getString("akka.quartz.defaultTimezone"));

    public static Props props() {
        return Props.create(MetadataSchedulerActor.class);
    }

    private void processMessage(Object message) {
        self().path();
        if (message instanceof CreateJob) {
            CreateJob createJob = (CreateJob)message;
            if (createJob.getMetadataQuery().getTriggerType() == TriggerType.CRON) {
                schedulerExtension.rescheduleJob(createJob.getMetadataQuery().getId().toString(), createJob.getActor(), createJob.getMessge(), description, createJob.getMetadataQuery().getTriggerSchedule(), cronCalender, timeZone);
            }
        } else if (message instanceof CancelJob) {
            schedulerExtension.cancelJob(((CancelJob) message).getMetadataQueryId().toString());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateJob.class, this::processMessage)
                .match(CancelJob.class, this::processMessage)
                .matchAny(o -> log.info("received unknown message [{}]", o.getClass().getName()))
                .build();
    }

}
