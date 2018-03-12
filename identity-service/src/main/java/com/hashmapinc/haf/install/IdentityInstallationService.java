package com.hashmapinc.haf.install;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class IdentityInstallationService {

    @Value("${install.data_dir}")
    private String dataDir;

    @Autowired
    private DatabaseSchemaService schemaService;

    public void performInstall() throws Exception {
        if (this.dataDir == null) {
            throw new RuntimeException("'install.data_dir' property should specified!");
        }
        if (!Files.isDirectory(Paths.get(this.dataDir))) {
            throw new RuntimeException("'install.data_dir' property value is not a valid directory!");
        }

        schemaService.createDatabaseSchema();
    }
}
