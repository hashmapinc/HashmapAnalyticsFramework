package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

public final class QueryMessage extends AbstractMessage {

    @Getter
    private final String query;

    @Getter
    private final MessageType messageType;

    public QueryMessage(String query, MetadataConfig metadataConfig, MessageType messageType) {
        super(metadataConfig);
        this.query = query;
        this.messageType = messageType;
    }
}
