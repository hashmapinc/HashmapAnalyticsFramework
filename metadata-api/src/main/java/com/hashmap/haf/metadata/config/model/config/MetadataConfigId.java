package com.hashmap.haf.metadata.config.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.config.model.UUIDBased;

import java.util.UUID;

public class MetadataConfigId extends UUIDBased {

    private static final long serialVersionUID = 8437100282679445365L;

    @JsonCreator
    public MetadataConfigId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static MetadataConfigId fromString(String metadataConfigId) {
        return new MetadataConfigId(UUID.fromString(metadataConfigId));
    }
}
