package com.hashmap.haf.metadata.config.model.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.config.model.UUIDBased;

import java.util.UUID;

public class MetadataQueryId extends UUIDBased {

    private static final long serialVersionUID = 7886135931067116998L;

    @JsonCreator
    public MetadataQueryId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static MetadataQueryId fromString(String queryId) {
        return new MetadataQueryId(UUID.fromString(queryId));
    }
}
