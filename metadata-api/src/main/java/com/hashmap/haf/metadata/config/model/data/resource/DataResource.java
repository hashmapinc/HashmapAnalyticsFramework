package com.hashmap.haf.metadata.config.model.data.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hashmap.haf.metadata.config.model.BaseData;
import com.hashmap.haf.metadata.config.model.UUIDBased;
import com.hashmap.haf.metadata.config.model.data.resource.jdbc.JdbcResource;
import com.hashmap.haf.metadata.config.model.data.resource.rest.RestResource;
import com.hashmap.haf.metadata.config.requests.IngestMetadataRequest;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdbcResource.class, name = "JDBC"),
        @JsonSubTypes.Type(value = RestResource.class, name = "REST")})
public abstract class DataResource<I extends UUIDBased> extends BaseData<I> {

    public DataResource() {
    }

    public DataResource(I id) {
        super(id);
    }

    public abstract void push(IngestMetadataRequest payload) throws Exception;

    public abstract Map pull(String query) throws Exception;

    public abstract boolean testConnection() throws Exception;
}
