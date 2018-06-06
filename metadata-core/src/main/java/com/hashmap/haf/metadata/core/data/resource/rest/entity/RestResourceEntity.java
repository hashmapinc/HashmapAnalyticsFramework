package com.hashmap.haf.metadata.core.data.resource.rest.entity;

import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.core.common.constants.ModelConstants;
import com.hashmap.haf.metadata.core.data.resource.DataResourceEntity;
import com.hashmap.haf.metadata.core.data.resource.rest.model.RestResource;
import com.hashmap.haf.metadata.core.data.resource.rest.model.RestResourceId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Slf4j
@Table(name = ModelConstants.REST_TABLE_NAME)
public class RestResourceEntity extends DataResourceEntity<RestResource> {

    @Column(name = ModelConstants.REST_URL)
    private String url;

    @Column(name = ModelConstants.REST_USERNAME)
    private String username;

    @Column(name = ModelConstants.REST_PASSWORD)
    private String password;

    public RestResourceEntity() {
        super();
    }

    public RestResourceEntity(RestResource restResource) {
        if (restResource.getId() != null) {
            this.setId(restResource.getId().getId());
        } else {
            this.setId(Generators.timeBasedGenerator().generate());
        }

        this.url = restResource.getUrl();
        this.username = restResource.getUsername();
        this.password = restResource.getPassword();
    }

    @Override
    public RestResource toData() {
        RestResource restResource = new RestResource(new RestResourceId(getId()));
        restResource.setUrl(url);
        restResource.setUsername(username);
        restResource.setPassword(password);
        return restResource;
    }
}