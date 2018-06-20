package com.hashmapinc.haf.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivateUserRequest {
    private String activateToken;
    private String password;

    @JsonCreator
    public ActivateUserRequest(@JsonProperty("activateToken") String activateToken,
                             @JsonProperty("password") String password) {
        this.activateToken = activateToken;
        this.password = password;
    }

    public String getActivateToken() {
        return activateToken;
    }

    public String getPassword() {
        return password;
    }
}
