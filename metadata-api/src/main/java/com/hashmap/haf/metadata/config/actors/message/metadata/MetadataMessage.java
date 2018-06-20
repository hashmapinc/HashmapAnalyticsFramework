package com.hashmap.haf.metadata.config.actors.message.metadata;

import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

public class MetadataMessage {

    @Getter
    private final MetadataConfig metadataConfig;

    @Getter
    private final MessageType messageType;

    public MetadataMessage(MetadataConfig metadataConfig, MessageType messageType) {
        this.metadataConfig = metadataConfig;
        this.messageType = messageType;
    }
}
