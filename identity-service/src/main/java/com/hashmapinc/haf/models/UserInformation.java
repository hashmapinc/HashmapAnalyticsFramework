package com.hashmapinc.haf.models;

import java.util.List;

public interface UserInformation {

    List<String> getAuthorities();

    boolean isEnabled();

    String getPassword();

    String getUserName();

    String getId();

    String getFirstName();

    String getLastName();

    String getTenantId();
}
