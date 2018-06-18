package com.hashmap.haf.metadata.config.actors.message.query;

import com.hashmap.haf.metadata.config.actors.message.AbstractQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;

public class DeleteQueryMsg extends AbstractQueryMsg {

    public DeleteQueryMsg(String query, MetadataConfigId metadataConfigId) {
        super(query, metadataConfigId);
    }
}
