package com.hashmap.haf.metadata.core.data.resource.jdbc.model;

import com.hashmap.haf.metadata.core.data.resource.DataResource;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JdbcResource extends DataResource<JdbcResourceId> {

    private String dbUrl;
    private String username;
    private String password;
    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

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
    public void push(Map payload) {
        //TODO : Will be implemented later, when we have JDBC as Sink for Metadata
    }

    @Override
    public Map pull(String query) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = createConnection();
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                payload.put(rs.getString(1), rs.getObject(2));
            }
        } finally {
            try {
                if(statement != null)
                    statement.close();
            } catch(SQLException se){
                log.info("Exection : [{}]", se.getMessage());
                se.printStackTrace();
            }
            try {
                if(connection != null)
                    connection.close();
            }catch(SQLException se){
                log.info("Exection : [{}]", se.getMessage());
                se.printStackTrace();
            }
        }
        return payload;
    }

    @Override
    public boolean testConnection() throws Exception {
        Connection connection = null;
        try {
            connection = createConnection();
            if (connection != null) {
                return true;
            }
        } finally {
            if(connection != null) {
                connection.close();
            }
        }
        return false;
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
        return connection;
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
