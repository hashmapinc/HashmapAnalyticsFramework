package com.hashmapinc.haf.models;

import java.util.UUID;

public class UserCredentials {

    private UUID id;
    private UUID userId;
    private String password;
    private ActivationType type;
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

    public ActivationType getType() {
        return type;
    }

    public void setType(ActivationType type) {
        this.type = type;
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
