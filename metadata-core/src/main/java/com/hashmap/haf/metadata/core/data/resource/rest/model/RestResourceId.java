package com.hashmap.haf.metadata.core.data.resource.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.core.common.data.id.UUIDBased;

import java.util.UUID;

public class RestResourceId extends UUIDBased {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public RestResourceId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static RestResourceId fromString(String restSinkId) {
        return new RestResourceId(UUID.fromString(restSinkId));
    }
}
