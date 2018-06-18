package com.hashmap.haf.metadata.config.actor.message.metadata;

import com.hashmap.haf.metadata.config.actor.message.AbstractMetadataConfigMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;

public class CreateMetadataConfigMsg extends AbstractMetadataConfigMsg {

    public CreateMetadataConfigMsg(MetadataConfig metadataConfig) {
        super(metadataConfig);
    }
}
