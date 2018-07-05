package com.hashmap.haf.metadata.config.actors.message.metadata;

import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

public final class MetadataMessage extends AbstractMessage {

    @Getter
    private final MessageType messageType;

    public MetadataMessage(MetadataConfig metadataConfig, MessageType messageType) {
        super(metadataConfig);
        this.messageType = messageType;
    }
}
