package com.hashmap.haf.metadata.config.actors.message.scheduler;

import com.hashmap.haf.metadata.config.model.query.MetadataQueryId;
import lombok.Getter;

public final class CancelJob {

    @Getter
    private final MetadataQueryId metadataQueryId;

    public CancelJob(MetadataQueryId metadataQueryId) {
        this.metadataQueryId = metadataQueryId;
    }
}
