package com.hashmap.haf.metadata.config.actors.message.scheduler;

import lombok.Getter;

public final class CancelJob {

    @Getter
    private final String query;

    public CancelJob(String query) {
        this.query = query;
    }
}
