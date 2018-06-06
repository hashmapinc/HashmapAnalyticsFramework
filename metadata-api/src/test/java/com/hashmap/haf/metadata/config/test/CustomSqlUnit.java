package com.hashmap.haf.metadata.config.test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class CustomSqlUnit extends ExternalResource {

    private List<String> sqlFiles;
    private final String dropAllTablesSqlFile;
    private final List<String> upgradeFiles;
    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;
    //private final String upgradePath;

    public CustomSqlUnit(List<String> sqlFiles, String dropAllTablesSqlFile, String configurationFileName, List<String> upgradeFiles) {
        log.error("In CustomSqlUnit");
        this.sqlFiles = sqlFiles;
        this.dropAllTablesSqlFile = dropAllTablesSqlFile;
        this.upgradeFiles = upgradeFiles;
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
        List<String> files = new ArrayList<>();
        files.addAll(sqlFiles);
        files.addAll(upgradeFiles);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            for (String sqlFile : files) {
                URL sqlFileUrl = Resources.getResource(sqlFile);
                String sql = Resources.toString(sqlFileUrl, Charsets.UTF_8);
                log.error("SQL : " + sql);
                conn.createStatement().execute(sql);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Unable to start embedded hsqldb. Reason: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
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
            throw new RuntimeException("Unable to clean up embedded hsqldb. Reason: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private Integer stripExtensionFromName(String fileName) {
        return Integer.parseInt(fileName.substring(0, fileName.indexOf(".sql")));
    }
}
