package com.hashmapinc.haf.entity;


import com.hashmapinc.haf.constants.ModelConstants;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.utils.UUIDConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = ModelConstants.USER_CREDENTIALS_TABLE)
public class UserCredentialsEntity implements Serializable{

    @Id
    @Column(name = ModelConstants.ID_PROPERTY)
    private String id;

    @Column(name = ModelConstants.USER_CREDENTIALS_PASSWORD_PROPERTY)
    private String password;

    @Column(name = ModelConstants.USER_CREDENTIALS_USER_ID_PROPERTY, unique = true)
    private String userId;

    @Column(name = ModelConstants.USER_CREDENTIALS_ACT_TOKEN_PROPERTY)
    private String activationToken;

    @Column(name = ModelConstants.USER_CREDENTIALS_RESET_TOKEN_PROPERTY, unique = true)
    private String resetToken;


    public UserCredentialsEntity() {
    }

    public UserCredentialsEntity(UserCredentials credentials){
        if (credentials.getId() != null) {
            this.setId(UUIDConverter.fromTimeUUID(credentials.getId()));
        }

        if (credentials.getUserId() != null) {
            this.userId = UUIDConverter.fromTimeUUID(credentials.getUserId());
        }
        this.password = credentials.getPassword();
        this.activationToken = credentials.getActivationToken();
        this.resetToken = credentials.getResetToken();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserCredentials toData() {
        UserCredentials userCredentials = new UserCredentials(UUIDConverter.fromString(getId()));
        userCredentials.setUserId(UUIDConverter.fromString(userId));
        userCredentials.setPassword(password);
        userCredentials.setActivationToken(activationToken);
        userCredentials.setResetToken(resetToken);
        return userCredentials;
    }
}
