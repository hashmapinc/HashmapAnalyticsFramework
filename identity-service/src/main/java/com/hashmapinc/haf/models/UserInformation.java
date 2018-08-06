package com.hashmapinc.haf.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface UserInformation extends Serializable {

    List<String> getAuthorities();

    boolean isEnabled();

    String getUserName();

    UUID getId();

    String getFirstName();

    String getLastName();

    String getTenantId();

    String getCustomerId();

    String getClientId();
}
