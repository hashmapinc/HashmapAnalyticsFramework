package com.hashmapinc.haf.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmapinc.haf.models.User;

public class CreateUserResponse {

    private final User user;
    private final String activationToken;

    @JsonCreator
    public CreateUserResponse(@JsonProperty("user") User user, @JsonProperty("activationToken") String activationToken) {
        this.user = user;
        this.activationToken = activationToken;
    }

    public User getUser() {
        return user;
    }

    public String getActivationToken() {
        return activationToken;
    }
}
