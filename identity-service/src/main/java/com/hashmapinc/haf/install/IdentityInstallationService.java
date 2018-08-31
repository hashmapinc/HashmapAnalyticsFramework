package com.hashmapinc.haf.install;

import com.datastax.driver.core.utils.UUIDs;
import com.hashmapinc.haf.exceptions.InstallationException;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.services.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class IdentityInstallationService {

    @Autowired
    private DatabaseSchemaService schemaService;

    @Autowired
    private UserDetailsService userService;

    public void performInstall(){
        try {
            log.info("Starting Identity Installation...");

            schemaService.createDatabaseSchema();
            createDemoUser();

            log.info("Finished Identity Installation...");
        }catch (Exception e){
            throw new InstallationException("Error occurred while performing install", e);
        }
    }

    private void createDemoUser() {
        String userName = "demo@identity.com";
        String clientId = "identity-service";
        UserInformation userInformation = userService.loadUserByUsername(userName, clientId);
        if(userInformation != null)
            return;

        User user = new User(UUIDs.timeBased());
        user.setClientId(clientId);
        user.setUserName(userName);
        user.setTenantId("HashmapInc");
        user.setAuthorities(Arrays.asList("admin", "user"));
        user.setEnabled(true);
        user.setFirstName("Demo");

        User savedUser = userService.save(user);
        UserCredentials credentials = userService.findCredentialsByUserId(savedUser.getId());
        credentials.setPassword("demo");
        userService.saveUserCredentials(credentials);
    }
}
