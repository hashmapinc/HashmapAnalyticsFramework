package com.hashmap.haf.metadata.core.data.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hashmap.haf.metadata.core.common.data.BaseData;
import com.hashmap.haf.metadata.core.common.data.id.UUIDBased;
import com.hashmap.haf.metadata.core.data.resource.jdbc.model.JdbcResource;
import com.hashmap.haf.metadata.core.data.resource.rest.model.RestResource;

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

    public abstract void push();

    public abstract void pull();
}
