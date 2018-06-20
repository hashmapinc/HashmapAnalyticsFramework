package com.hashmap.haf.metadata.config.actors.message;

import com.hashmap.haf.metadata.config.model.MetadataConfig;
import lombok.Getter;

public class AbstractQueryMsg {

    @Getter
    private final String query;

    @Getter
    private final MetadataConfig metadataConfig;

    public AbstractQueryMsg(String query, MetadataConfig metadataConfig) {
        this.query = query;
        this.metadataConfig = metadataConfig;
    }
}
