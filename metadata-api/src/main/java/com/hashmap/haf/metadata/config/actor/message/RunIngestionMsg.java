package com.hashmap.haf.metadata.config.actor.message;

import com.hashmap.haf.metadata.config.model.MetadataConfig;

public final class RunIngestionMsg extends AbstractMetadataConfigMsg {

    public RunIngestionMsg(MetadataConfig metadataConfig) {
        super(metadataConfig);
    }
}
