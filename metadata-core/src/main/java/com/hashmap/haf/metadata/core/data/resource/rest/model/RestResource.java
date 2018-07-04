package com.hashmap.haf.metadata.core.data.resource.rest.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmap.haf.metadata.core.data.resource.DataResource;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Slf4j
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
    public void push(Map payload) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(new ObjectMapper().writeValueAsString(payload));
            writer.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            br.close();
            connection.disconnect();

        } catch (MalformedURLException e) {
            log.info("Malformed URL exception : [{}]", e.getMessage());
        } catch (IOException e) {
            log.info("IOException : [{}]", e.getMessage());
        }
    }

    @Override
    public Map pull(String query) {
        //TODO : Will be implemented when we have REST as a Metadata Source
        return Collections.emptyMap();
    }

    @Override
    public boolean testConnection() {
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
