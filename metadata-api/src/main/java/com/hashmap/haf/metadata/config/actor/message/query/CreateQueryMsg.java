package com.hashmap.haf.metadata.config.actor.message.query;

import com.hashmap.haf.metadata.config.actor.message.AbstractQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;

public class CreateQueryMsg extends AbstractQueryMsg {

    public CreateQueryMsg(String query, MetadataConfigId metadataConfigId) {
        super(query, metadataConfigId);
    }
}
