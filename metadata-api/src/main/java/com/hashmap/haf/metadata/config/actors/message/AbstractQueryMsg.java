package com.hashmap.haf.metadata.config.actors.message;

import com.hashmap.haf.metadata.config.model.MetadataConfigId;
import lombok.Getter;

public class AbstractQueryMsg {

    @Getter
    private final String query;

    @Getter
    private final MetadataConfigId metadataConfigId;

    public AbstractQueryMsg(String query, MetadataConfigId metadataConfigId) {
        this.query = query;
        this.metadataConfigId = metadataConfigId;
    }
}
