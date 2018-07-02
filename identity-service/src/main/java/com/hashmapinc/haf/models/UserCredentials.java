package com.hashmapinc.haf.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserCredentials {

    private UUID id;
    private UUID userId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String activationToken;
    private String resetToken;

    public UserCredentials(){
    }


    public UserCredentials(UUID id){
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
