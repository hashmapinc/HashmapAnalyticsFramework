package com.hashmap.haf.metadata.config.install;

import com.hashmap.haf.metadata.config.exceptions.MetadataInstallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("install")
public class MetadataServiceInstall {

    @Autowired
    private DatabaseSchemaService databaseSchemaService;

    public void performInstall() {
        try {
            log.info("Starting Metadata Ingestion Service Installation...");

            databaseSchemaService.createDatabaseSchema();

            log.info("Server Started...");
        } catch (Exception e) {
            log.error("Unexpected error during Metadata Ingestion Service installation!", e);
            throw new MetadataInstallException("Unexpected error during Metadata Ingestion Service installation!", e);
        }
    }
}
