package com.hashmap.haf.metadata.config.actors.message;

import com.hashmap.haf.metadata.config.model.config.MetadataConfig;
import lombok.Getter;

public abstract class AbstractMessage {

    @Getter
    private final MetadataConfig metadataConfig;

    public AbstractMessage(MetadataConfig metadataConfig) {
        this.metadataConfig = metadataConfig;
    }
}
