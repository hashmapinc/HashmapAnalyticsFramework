package com.hashmap.haf.metadata.config.model.data.resource.jdbc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.config.model.UUIDBased;

import java.util.UUID;

public class JdbcResourceId extends UUIDBased {


    private static final long serialVersionUID = 8176568480029736088L;

    @JsonCreator
    public JdbcResourceId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static JdbcResourceId fromString(String jdbcSourceId) {
        return new JdbcResourceId(UUID.fromString(jdbcSourceId));
    }
}
