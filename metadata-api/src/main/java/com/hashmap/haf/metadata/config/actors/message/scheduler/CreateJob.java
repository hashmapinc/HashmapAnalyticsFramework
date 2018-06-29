package com.hashmap.haf.metadata.config.actors.message.scheduler;

import akka.actor.ActorRef;
import com.hashmap.haf.metadata.core.trigger.TriggerType;
import lombok.Getter;

public final class CreateJob {

    @Getter
    private final String query;

    @Getter
    private final TriggerType triggerType;

    @Getter
    private final String triggerSchedule;

    @Getter
    private final ActorRef actor;

    @Getter
    private final Object messge;

    public CreateJob(String query, TriggerType triggerType, String triggerSchedule, ActorRef actor, Object messge) {
        this.query = query;
        this.triggerType = triggerType;
        this.triggerSchedule = triggerSchedule;
        this.actor = actor;
        this.messge = messge;
    }
}
