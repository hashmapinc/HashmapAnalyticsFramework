package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

public class QueryMessage {

    @Getter
    private final String query;

    @Getter
    private final MetadataConfig metadataConfig;

    @Getter
    private final MessageType messageType;

    public QueryMessage(String query, MetadataConfig metadataConfig, MessageType messageType) {
        this.query = query;
        this.metadataConfig = metadataConfig;
        this.messageType = messageType;
    }
}
