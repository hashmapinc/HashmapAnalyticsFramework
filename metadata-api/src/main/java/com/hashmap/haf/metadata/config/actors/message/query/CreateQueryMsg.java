package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.AbstractQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;

public class CreateQueryMsg extends AbstractQueryMsg {

    public CreateQueryMsg(String query, MetadataConfig metadataConfig) {
        super(query, metadataConfig);
    }
}
