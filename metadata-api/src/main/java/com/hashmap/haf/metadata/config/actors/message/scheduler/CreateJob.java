package com.hashmap.haf.metadata.config.actors.message.scheduler;

import akka.actor.ActorRef;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import lombok.Getter;

public final class CreateJob {

    @Getter
    MetadataQuery metadataQuery;

    @Getter
    private final ActorRef actor;

    @Getter
    private final Object messge;

    public CreateJob(MetadataQuery metadataQuery, ActorRef actor, Object messge) {
        this.metadataQuery = metadataQuery;
        this.actor = actor;
        this.messge = messge;
    }
}
