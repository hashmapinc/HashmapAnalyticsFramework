package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.AbstractQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfig;

public class UpdateQueryMsg extends AbstractQueryMsg {

    public UpdateQueryMsg(String query, MetadataConfig metadataConfig) {
        super(query, metadataConfig);
    }
}
