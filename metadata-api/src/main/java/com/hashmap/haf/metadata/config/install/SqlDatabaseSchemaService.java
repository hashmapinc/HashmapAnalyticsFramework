package com.hashmap.haf.metadata.config.install;

import com.hashmap.haf.metadata.config.exceptions.MetadataInstallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Slf4j
@Service
@Profile("install")
public class SqlDatabaseSchemaService implements DatabaseSchemaService {

    private static final String SQL_DIR = "sql";
    private static final String SCHEMA_SQL = "schema.sql";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public void createDatabaseSchema() {
        log.info("Installing SQL DataBase schema...");

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword)) {
            InputStream inputStream = new ClassPathResource(String.format("%s/%s", SQL_DIR, SCHEMA_SQL)).getInputStream();
            String sql = new String(FileCopyUtils.copyToByteArray(inputStream), Charset.forName("UTF-8"));
            try (Statement statement = conn.createStatement()) {
                statement.execute(sql);
            }
        } catch (Exception ex) {
            throw new MetadataInstallException("Error while applying database schema", ex);
        }
    }
}