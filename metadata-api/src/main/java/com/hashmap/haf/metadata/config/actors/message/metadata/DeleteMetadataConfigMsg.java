package com.hashmap.haf.metadata.config.actors.message.metadata;

import com.hashmap.haf.metadata.config.actors.message.AbstractMetadataConfigMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;

public class DeleteMetadataConfigMsg extends AbstractMetadataConfigMsg {

    public DeleteMetadataConfigMsg(MetadataConfig metadataConfig) {
        super(metadataConfig);
    }
}
