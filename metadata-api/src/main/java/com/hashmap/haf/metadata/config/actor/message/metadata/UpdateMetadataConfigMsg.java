package com.hashmap.haf.metadata.config.actor.message.metadata;

import com.hashmap.haf.metadata.config.actor.message.AbstractMetadataConfigMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;

public class UpdateMetadataConfigMsg extends AbstractMetadataConfigMsg {

    public UpdateMetadataConfigMsg(MetadataConfig metadataConfig) {
        super(metadataConfig);
    }
}
