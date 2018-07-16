package com.hashmapinc.haf.install;

import com.hashmapinc.haf.exceptions.SchemaCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Service
public class SqlDatabaseSchemService implements DatabaseSchemaService{
    private static final String SQL_DIR = "sql";
    private static final String SCHEMA_SQL = "identity-schema.sql";

    @Value("${install.data_dir}")
    private String dataDir;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    @SuppressWarnings("squid:S2077")
    public void createDatabaseSchema(){

        Path schemaFile = Paths.get(this.dataDir, SQL_DIR, SCHEMA_SQL);
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword)) {
            String sql = new String(Files.readAllBytes(schemaFile), Charset.forName("UTF-8"));
            try(Statement statement = conn.createStatement()) {
                statement.execute(sql);
            }
        }catch (Exception ex){
            throw new SchemaCreationException("Error while applying database schema", ex);
        }

    }
}
