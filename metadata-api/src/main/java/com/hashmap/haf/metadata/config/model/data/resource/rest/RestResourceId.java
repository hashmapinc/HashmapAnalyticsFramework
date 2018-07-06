package com.hashmap.haf.metadata.config.model.data.resource.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.config.model.UUIDBased;

import java.util.UUID;

public class RestResourceId extends UUIDBased {


    private static final long serialVersionUID = 3957087907289947369L;

    @JsonCreator
    public RestResourceId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static RestResourceId fromString(String restSinkId) {
        return new RestResourceId(UUID.fromString(restSinkId));
    }
}
