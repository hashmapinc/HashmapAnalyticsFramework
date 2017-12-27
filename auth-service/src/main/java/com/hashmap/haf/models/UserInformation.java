package com.hashmap.haf.models;

import java.util.List;

public interface UserInformation {

    List<String> getAuthorities();

    boolean isEnabled();

    String getPassword();
}
