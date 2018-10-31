package com.hashmap.haf.metadata.config.model.data.resource.jdbc;

import com.hashmap.haf.metadata.config.model.data.resource.DataResource;
import com.hashmap.haf.metadata.config.requests.IngestMetadataRequest;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public class JdbcResource extends DataResource<JdbcResourceId> {

    private String dbUrl;
    private String username;
    private String password;

    @Transient
    private DataSource dataSource;

    public JdbcResource() {
        super();
    }

    public JdbcResource(JdbcResourceId id) {
        super(id);
    }

    public JdbcResource(JdbcResource jdbcResource) {
        this.dbUrl = jdbcResource.dbUrl;
        this.username = jdbcResource.username;
        this.password = jdbcResource.password;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void push(IngestMetadataRequest payload) {
        //TODO : Will be implemented later, when we have JDBC as Sink for Metadata
    }

    @Override
    public Map pull(String query) throws Exception {
        Map<String, Object> payload;

        if (dataSource == null) {
            dataSource = getDataSource();
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        payload = jdbcTemplate.query(query, new ResultSetExtractor<Map<String, Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet rs) throws SQLException {
                Map<String, Object> data = new HashMap<>();
                while (rs.next()) {
                    data.put(rs.getString(1), rs.getObject(2));
                }
                return data;
            }
        });

        return payload;
    }

    @Override
    public boolean testConnection() throws Exception {
        Connection connection = null;
        if (dataSource == null) {
            dataSource = getDataSource();
        }
        connection = dataSource.getConnection();
        boolean connected = connection != null;
        connection.close();
        return connected;
    }

    private DataSource getDataSource() {
        String decodedPassword = new String(Base64.getDecoder().decode(this.password));
        return DataSourceBuilder
                .create()
                .url(this.dbUrl)
                .username(this.username)
                .password(decodedPassword)
                .driverClassName(getJdbcDriver())
                .build();
    }

    private String getJdbcDriver() {
        if (this.dbUrl.contains("mysql")) {
            return  "com.mysql.jdbc.Driver";
        } else if (this.dbUrl.contains("postgres")) {
            return "org.postgresql.Driver";
        }
        return null;
    }

    @Override
    public String toString() {
        return "JdbcResource{" +
                "dbUrl=" + dbUrl +
                ", username=" + username +
                ", password=" + password +
                '}';
    }
}
