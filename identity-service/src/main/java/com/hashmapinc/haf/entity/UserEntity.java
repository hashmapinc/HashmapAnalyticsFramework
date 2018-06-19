package com.hashmapinc.haf.entity;


import com.hashmapinc.haf.constants.ModelConstants;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.utils.UUIDConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    @Column(name = ModelConstants.USER_PASSWORD_PROPERTY)
    private String password;

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

    @ElementCollection(fetch = FetchType.EAGER)
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
        this.password = user.getPassword();
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
        //user.setCreatedTime(UUIDs.unixTimestamp(getId()));
        user.setAuthorities(authorities);
        user.setPermissions(permissions);
        if (tenantId != null) {
            user.setTenantId(tenantId);
        }
        user.setUserName(userName);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setClientId(clientId);
        user.setCustomerId(customerId);
        user.setAdditionalDetails(additionalDetails);
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;

        UserEntity that = (UserEntity) o;

        if (enabled != that.enabled) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (permissions != null ? !permissions.equals(that.permissions) : that.permissions != null ) return false;
        return authorities != null ? authorities.equals(that.authorities) : that.authorities == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }
}
