package com.hashmap.haf.metadata.config.install;

import com.hashmap.haf.metadata.config.exceptions.MetadataInstallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class MetadataServiceInstall {

    @Value("${install.data_dir}")
    private String dataDir;

    @Autowired
    private DatabaseSchemaService databaseSchemaService;

    public void performInstall() {
        try {
            log.info("Starting Metadata Ingestion Service Installation...");

            if (this.dataDir == null) {
                throw new RuntimeException("'install.data_dir' property should specified!");
            }
            if (!Files.isDirectory(Paths.get(this.dataDir))) {
                throw new RuntimeException("'install.data_dir' property value is not a valid directory!");
            }

            log.info("Installing DataBase schema...");

            databaseSchemaService.createDatabaseSchema();
        } catch (Exception e) {
            log.error("Unexpected error during Metadata Ingestion Service installation!", e);
            throw new MetadataInstallException("Unexpected error during Metadata Ingestion Service installation!", e);
        }
    }
}
