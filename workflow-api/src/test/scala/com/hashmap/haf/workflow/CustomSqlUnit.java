package com.hashmap.haf.workflow;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class CustomSqlUnit extends ExternalResource {

    private final List<String> sqlFiles;
    private final String dropAllTablesSqlFile;
    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;

    private static Logger logger = LoggerFactory.getLogger(CustomSqlUnit.class);

    public CustomSqlUnit(List<String> sqlFiles, String dropAllTablesSqlFile, String configurationFileName) {
        this.sqlFiles = sqlFiles;
        this.dropAllTablesSqlFile = dropAllTablesSqlFile;
        final Properties properties = new Properties();
        try (final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(configurationFileName)) {
            properties.load(stream);
            this.dbUrl = properties.getProperty("spring.datasource.url");
            this.dbUserName = properties.getProperty("spring.datasource.username");
            this.dbPassword = properties.getProperty("spring.datasource.password");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void before() {
        cleanUpDb();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            for (String sqlFile : sqlFiles) {
                URL sqlFileUrl = Resources.getResource(sqlFile);
                String sql = Resources.toString(sqlFileUrl, Charsets.UTF_8);
                conn.createStatement().execute(sql);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Unable to start embedded postgres. Reason: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void after() {
        cleanUpDb();
    }

    private void cleanUpDb() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            URL dropAllTableSqlFileUrl = Resources.getResource(dropAllTablesSqlFile);
            String dropAllTablesSql = Resources.toString(dropAllTableSqlFileUrl, Charsets.UTF_8);
            conn.createStatement().execute(dropAllTablesSql);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Unable to clean up embedded postgres. Reason: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
