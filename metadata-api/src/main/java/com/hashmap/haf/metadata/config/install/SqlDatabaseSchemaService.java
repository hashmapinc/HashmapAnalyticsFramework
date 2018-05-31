package com.hashmap.haf.metadata.config.install;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Slf4j
@Service
public class SqlDatabaseSchemaService implements DatabaseSchemaService {

    private static final String SQL_DIR = "sql";
    private static final String SCHEMA_SQL = "schema.sql";

    @Value("${install.data_dir}")
    private String dataDir;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public void createDatabaseSchema() throws Exception {
        log.info("Installing SQL DataBase schema...");

        Path schemaFile = Paths.get(this.dataDir, SQL_DIR, SCHEMA_SQL);

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
             Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(schemaFile), Charset.forName("UTF-8"));

            stmt.execute(sql);
        }
    }
}
