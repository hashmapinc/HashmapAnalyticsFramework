package com.hashmap.haf.metadata.config.model.data.resource.rest;

import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class RestResource extends DataResource<RestResourceId> {

    @Autowired
    RestTemplate restTemplate;

    private String url;
    private String username;
    private String password;

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
    public void push(Map payload) throws Exception {
        restTemplate.postForEntity(this.url, payload, Void.class);
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
