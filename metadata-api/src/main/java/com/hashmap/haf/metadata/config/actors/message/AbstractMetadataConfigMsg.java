package com.hashmap.haf.metadata.config.actors.message;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

abstract public class AbstractMetadataConfigMsg {

    @Getter
    private final MetadataConfig metadataConfig;

    public AbstractMetadataConfigMsg(MetadataConfig metadataConfig) {
        this.metadataConfig = metadataConfig;
    }
}
