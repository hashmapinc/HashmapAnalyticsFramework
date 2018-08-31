package com.hashmapinc.haf.constants;

import com.datastax.driver.core.utils.UUIDs;

import java.util.UUID;

public class ModelConstants {
    private ModelConstants() {
    }

    public static final UUID NULL_UUID = UUIDs.startOf(0);
    public static final String USERS_TABLE = "haf_users";
    public static final String ID_PROPERTY = "id";
    public static final String USER_NAME_PROPERTY = "user_name";
    public static final String TENANT_ID_PROPERTY = "tenant_id";
    public static final String CUSTOMER_ID_PROPERTY = "customer_id";
    public static final String CLIENT_ID_PROPERTY = "client_id";
    public static final String USER_FIRST_NAME_PROPERTY = "first_name";
    public static final String USER_LAST_NAME_PROPERTY = "last_name";
    public static final String USER_AUTHORITIES_COLUMN = "authorities_id";
    public static final String USER_DETAILS_KEY_COLUMN = "key_name";
    public static final String USER_DETAILS_VALUE_COLUMN = "key_value";
    public static final String USER_JOIN_COLUMN = "user_id";

    public static final String USER_ENABLED_PROPERTY = "enabled";
    public static final String USER_AUTHORITIES_TABLE = "haf_user_authorities";
    public static final String USER_ADDITIONAL_DETAILS_TABLE = "haf_user_details";

    public static final String USER_CREDENTIALS_TABLE = "haf_user_credentials";
    public static final String USER_CREDENTIALS_SECRET_PROPERTY = "password";
    public static final String USER_CREDENTIALS_USER_ID_PROPERTY = "user_id";
    public static final String USER_CREDENTIALS_ACT_TOKEN_PROPERTY = "activation_token";
    public static final String USER_CREDENTIALS_RESET_TOKEN_PROPERTY = "reset_token";
}
