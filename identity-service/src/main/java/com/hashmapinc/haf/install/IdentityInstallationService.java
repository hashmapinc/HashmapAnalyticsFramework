package com.hashmapinc.haf.install;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class IdentityInstallationService {

    @Value("${install.data_dir}")
    private String dataDir;

    @Autowired
    private DatabaseSchemaService schemaService;

    @Autowired
    private UserDetailsService userService;

    public void performInstall() throws Exception {
        if (this.dataDir == null) {
            throw new RuntimeException("'install.data_dir' property should specified!");
        }
        if (!Files.isDirectory(Paths.get(this.dataDir))) {
            throw new RuntimeException("'install.data_dir' property value is not a valid directory!");
        }

        schemaService.createDatabaseSchema();

        User user = new User(UUIDs.timeBased());
        user.setClientId("identity-service");
        user.setUserName("demo");
        user.setTenantId("hashmapInc");
        user.setAuthorities(Arrays.asList("admin", "user"));
        user.setEnabled(true);
        user.setFirstName("demo");

        User savedUser = userService.save(user);

        UserCredentials credentials = userService.findCredentialsByUserId(savedUser.getId());
        credentials.setPassword("demo");

        userService.saveUserCredentials(credentials);
    }
}
