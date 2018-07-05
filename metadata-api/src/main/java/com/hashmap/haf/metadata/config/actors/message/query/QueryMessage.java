package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.AbstractMessage;
import com.hashmap.haf.metadata.config.actors.message.MessageType;
import com.hashmap.haf.metadata.config.model.MetadataConfig;
import com.hashmap.haf.metadata.config.model.MetadataQuery;
import lombok.Getter;

public final class QueryMessage extends AbstractMessage {

    @Getter
    private final MetadataQuery metadataQuery;

    @Getter
    private final MessageType messageType;

    public QueryMessage(MetadataQuery metadataQuery, MetadataConfig metadataConfig, MessageType messageType) {
        super(metadataConfig);
        this.metadataQuery = metadataQuery;
        this.messageType = messageType;
    }
}
