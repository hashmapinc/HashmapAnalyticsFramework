package com.hashmap.haf.metadata.config.model.data.resource.rest;

import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import com.hashmap.haf.metadata.config.requests.IngestMetadataRequest;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class RestResource extends DataResource<RestResourceId> {

    private String url;

    @Transient
    @Setter
    private RestTemplate restTemplate;

    public RestResource() {
        super();
    }

    public RestResource(RestResourceId id) {
        super(id);
    }

    public RestResource(RestResource restResource) {
        this.url = restResource.url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void push(IngestMetadataRequest payload) throws Exception {
        log.trace("Executing RestResource.push for payload [{}]", payload);
        restTemplate.postForEntity(this.url, payload, Object.class);
    }

    @Override
    public Map pull(String query) throws Exception {
        //TODO : Will be implemented when we have REST as a Metadata Source
        return Collections.emptyMap();
    }

    @Override
    public boolean testConnection() throws Exception {
        return false;
    }

    @Override
    public String toString() {
        return "RestResource{" +
                "dbUrl=" + url +
                '}';
    }
}
