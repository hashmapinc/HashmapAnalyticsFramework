package com.hashmapinc.haf.entity;


import com.hashmapinc.haf.constants.ModelConstants;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.utils.UUIDConverter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@Entity
@Table(name = ModelConstants.USERS_TABLE)
public class UserEntity implements Serializable{

    @Transient
    private static final long serialVersionUID = 9135513967525611597L;

    @Id
    @Column(name = ModelConstants.ID_PROPERTY)
    private String id;

    @Column(name = ModelConstants.USER_NAME_PROPERTY)
    private String userName;

    @Column(name = ModelConstants.TENANT_ID_PROPERTY)
    private String tenantId;

    @Column(name = ModelConstants.CUSTOMER_ID_PROPERTY)
    private String customerId;

    @Column(name = ModelConstants.CLIENT_ID_PROPERTY)
    private String clientId;

    @Column(name = ModelConstants.USER_ENABLED_PROPERTY)
    private boolean enabled;

    @Column(name = ModelConstants.USER_FIRST_NAME_PROPERTY)
    private String firstName;

    @Column(name = ModelConstants.USER_LAST_NAME_PROPERTY)
    private String lastName;

    @ElementCollection()
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = ModelConstants.USER_PERMISSIONS_TABLE, joinColumns = @JoinColumn(name = ModelConstants.USER_JOIN_COLUMN))
    @Column(name = ModelConstants.USER_PERMISSIONS_COLUMN)
    private List<String> permissions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = ModelConstants.USER_AUTHORITIES_TABLE, joinColumns = @JoinColumn(name = ModelConstants.USER_JOIN_COLUMN))
    @Column(name = ModelConstants.USER_AUTHORITIES_COLUMN)
    private List<String> authorities;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = ModelConstants.USER_DETAILS_KEY_COLUMN)
    @Column(name = ModelConstants.USER_DETAILS_VALUE_COLUMN)
    @CollectionTable(name=ModelConstants.USER_ADDITIONAL_DETAILS_TABLE, joinColumns=@JoinColumn(name = ModelConstants.USER_JOIN_COLUMN))
    private Map<String, String> additionalDetails;

    public UserEntity() {
    }

    public UserEntity(User user) {
        if (user.getId() != null) {
            this.setId(UUIDConverter.fromTimeUUID(user.getId()));
        }
        this.userName = user.getUserName();
        this.enabled = user.isEnabled();
        this.authorities = user.getAuthorities();
        this.permissions = user.getPermissions();
        if (user.getTenantId() != null) {
            this.tenantId = user.getTenantId();
        }
        if (user.getClientId() != null) {
            this.clientId = user.getClientId();
        }
        if (user.getCustomerId() != null) {
            this.customerId = user.getCustomerId();
        }
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.additionalDetails = user.getAdditionalDetails();
    }

    protected void setId(String id){
        this.id = id;
    }

    protected String getId(){
        return this.id;
    }

    public String getClientId() {
        return clientId;
    }

    public User toData() {
        User user = new User(UUIDConverter.fromString(getId()));
        user.setAuthorities(authorities);
        user.setPermissions(permissions);
        if (tenantId != null) {
            user.setTenantId(tenantId);
        }
        user.setUserName(userName);
        user.setEnabled(enabled);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setClientId(clientId);
        user.setCustomerId(customerId);
        user.setAdditionalDetails(additionalDetails);
        return user;
    }
}
