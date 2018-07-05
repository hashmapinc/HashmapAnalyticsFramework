package com.hashmapinc.haf.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmapinc.haf.models.ActivationType;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;

public class CreateUserRequest {

    private final User user;
    private final UserCredentials credentials;
    private final ActivationType activationType;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("user") User user,
                             @JsonProperty("credentials") UserCredentials credentials,
                             @JsonProperty("activationType") ActivationType activationType) {
        this.user = user;
        this.credentials = credentials;
        this.activationType = activationType;
    }

    public User getUser() {
        return user;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public ActivationType getActivationType() {
        return activationType;
    }
}
