package com.hashmap.haf.metadata.core.data.resource.rest.model;

import com.hashmap.haf.metadata.core.data.resource.DataResource;

public class RestResource extends DataResource<RestResourceId> {

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
    public void push() {

    }

    @Override
    public void pull() {

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
