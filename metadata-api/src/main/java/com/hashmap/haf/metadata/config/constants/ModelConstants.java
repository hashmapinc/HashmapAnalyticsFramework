package com.hashmap.haf.metadata.config.constants;

public class ModelConstants {

    private ModelConstants() {
    }

    /**
     * Generic constants.
     */
    public static final String ID_PROPERTY = "id";

    /**
     * Metadata Config Constants
     */
    public static final String METADATA_CONFIG_OWNER_ID = "owner_id";
    public static final String METADATA_CONFIG_TABLE_NAME = "metadata_config";
    public static final String METADATA_CONFIG_NAME = "name";

    /**
     * JDBC Source Constants
     */
    public static final String JDBC_TABLE_NAME = "metadata_resource_jdbc";
    public static final String JDBC_DBURL = "db_url";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password"; //NOSONAR

    /**
     * REST Sink Constants
     */
    public static final String REST_TABLE_NAME = "metadata_resource_rest";
    public static final String REST_URL = "url";

    /**
     * Metadata Query Constants
     */
    public static final String METADATA_QUERY_TABLE_NAME = "metadata_query";
    public static final String METADATA_QUERY = "query";
    public static final String METADATA_QUERY_TRIGGER_TYPE = "trigger_type";
    public static final String METADATA_QUERY_TRIGGER_SCHEDULE = "trigger_schdl";
    public static final String METADATA_QUERY_CONFIG_ID = "metadata_id";
}
