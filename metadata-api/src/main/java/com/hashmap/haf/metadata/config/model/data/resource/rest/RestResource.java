package com.hashmap.haf.metadata.config.model.data.resource.rest;

import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import com.hashmap.haf.metadata.config.requests.IngestMetadataRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class RestResource extends DataResource<RestResourceId> {

    private String url;
    private String username;
    private String password;

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
        this.username = restResource.username;
        this.password = restResource.password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                ", username=" + username +
                ", password=" + password +
                '}';
    }
}
