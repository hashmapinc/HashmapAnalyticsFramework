package com.hashmap.haf.metadata.config.actor.message.query;

import com.hashmap.haf.metadata.config.actor.message.AbstractQueryMsg;
import com.hashmap.haf.metadata.config.model.MetadataConfigId;

public class UpdateQueryMsg extends AbstractQueryMsg {

    public UpdateQueryMsg(String query, MetadataConfigId metadataConfigId) {
        super(query, metadataConfigId);
    }
}
