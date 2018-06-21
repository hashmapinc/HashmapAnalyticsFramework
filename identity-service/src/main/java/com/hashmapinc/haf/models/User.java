package com.hashmapinc.haf.models;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class User implements UserInformation, Serializable{
    private static final long serialVersionUID = -350874482962054954L;

    private UUID id;
    private String userName;
    private String tenantId;
    private String customerId;
    private String clientId;
    private String firstName;
    private String lastName;
    private List<String> authorities;
    private List<String> permissions;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private boolean enabled;

    public User(){
        this(UUIDs.timeBased());
    }

    public User(UUID id){
        this.id = id;
    }

    public User(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.tenantId = user.getTenantId();
        this.customerId = user.getCustomerId();
        this.clientId = user.getClientId();
        this.authorities = user.getAuthorities();
        this.permissions = user.getPermissions();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.enabled = user.isEnabled();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != null ? !getId().equals(user.getId()) : user.getId() != null) return false;
        if (getUserName() != null ? !getUserName().equals(user.getUserName()) : user.getUserName() != null)
            return false;
        if (getTenantId() != null ? !getTenantId().equals(user.getTenantId()) : user.getTenantId() != null)
            return false;
        if (getFirstName() != null ? !getFirstName().equals(user.getFirstName()) : user.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(user.getLastName()) : user.getLastName() != null)
            return false;
        if (getPermissions() != null ? !getPermissions().equals(user.getPermissions()) : user.getPermissions() != null)
            return false;
        return getAuthorities() != null ? getAuthorities().equals(user.getAuthorities()) : user.getAuthorities() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getTenantId() != null ? getTenantId().hashCode() : 0);
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getAuthorities() != null ? getAuthorities().hashCode() : 0);
        result = 31 * result + (getPermissions() != null ? getPermissions().hashCode() : 0);
        return result;
    }
}
