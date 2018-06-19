package com.hashmapinc.haf.entity;


import com.hashmapinc.haf.constants.ModelConstants;
import com.hashmapinc.haf.models.UserCredentials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = ModelConstants.USER_CREDENTIALS_TABLE)
public class UserCredentialsEntity implements Serializable{

    @Id
    @Column(name = ModelConstants.ID_PROPERTY)
    private UUID id;

    @Column(name = ModelConstants.USER_CREDENTIALS_PASSWORD_PROPERTY)
    private String password;

    @Column(name = ModelConstants.USER_CREDENTIALS_ACT_TOKEN_PROPERTY)
    private String activationToken;

    @Column(name = ModelConstants.USER_CREDENTIALS_RESET_TOKEN_PROPERTY)
    private String resetToken;

    @Column(name = ModelConstants.USER_CREDENTIALS_ACT_TYPE_PROPERTY)
    private String activationType;

    public UserCredentialsEntity(UserCredentials credentials){
        setId(credentials.getId());
        this.password = credentials.getPassword();
        this.activationType = credentials.getType().toValue();
        this.activationToken = credentials.getActivationToken();
        this.resetToken = credentials.getResetToken();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
