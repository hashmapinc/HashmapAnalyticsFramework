package com.hashmapinc.haf.models;

import java.util.List;
import java.util.UUID;

public interface UserInformation {

    List<String> getAuthorities();

    List<String> getPermissions();

    boolean isEnabled();

    String getUserName();

    UUID getId();

    String getFirstName();

    String getLastName();

    String getTenantId();

    String getCustomerId();

    String getClientId();
}
