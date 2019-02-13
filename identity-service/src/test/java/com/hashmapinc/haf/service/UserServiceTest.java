package com.hashmapinc.haf.service;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import com.hashmapinc.haf.utils.UUIDConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Instant;
import java.util.*;

import static com.hashmapinc.haf.constants.ModelConstants.NULL_UUID;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    private static final String clientId = "tempus";
    private final String TENANT_ADMIN = "TENANT_ADMIN";

    @Autowired
    private DatabaseUserDetailsService userService;

    @Test
    public void shouldReturnTrialUsersByClientIdAndAuthorities() {

        User trialTempusUser = createUser("trialUser",NULL_UUID.toString(),TENANT_ADMIN,true,clientId);
        User tempusUser = createUser("demo",NULL_UUID.toString(),TENANT_ADMIN,false,clientId);
        User identityUser = createUser("identity",NULL_UUID.toString(),TENANT_ADMIN,true,"identity");

        List<User> tenantAdminUsers = userService.findByClientIdAndAuthoritiesAndAdditionalDetails(clientId,TENANT_ADMIN,"trialAccount","true");
        Assert.assertEquals(tenantAdminUsers.size(),1);

        deleteUser(trialTempusUser.getId());
        deleteUser(tempusUser.getId());
        deleteUser(identityUser.getId());
    }

    @Test
    public void shouldReturnAllTenantCustomers() throws Exception {
        User savedAdminUser = createUser("trialUser", UUID.randomUUID().toString(), TENANT_ADMIN, true, clientId);
        String CUSTOMER_USER = "CUSTOMER_USER";
        User savedCustomer = createUser("bob", savedAdminUser.getTenantId(), CUSTOMER_USER, false,clientId);

        List<User> foundTenantAdminUsers = userService.findByTenantId(savedAdminUser.getTenantId());

        Assert.assertEquals(foundTenantAdminUsers.size(), 2);

        deleteUser(savedAdminUser.getId());
        deleteUser(savedCustomer.getId());
    }

    private User createUser(String userName , String tenantId , String authority ,boolean trialAccount, String clientId){

        User user = new User();
        user.setUserName(userName);
        user.setEnabled(true);
        user.setAuthorities(Arrays.asList(authority));
        user.setClientId(clientId);
        user.setTenantId(tenantId);
        user.setCustomerId(NULL_UUID.toString());

        if(trialAccount) {
            Map<String,String> additionalDetails = new HashMap<>();
            String IS_TRIAL_ACCOUNT = "trialAccount";
            additionalDetails.put(IS_TRIAL_ACCOUNT,"true");
            additionalDetails.put("phoneNo", "123456789");
            additionalDetails.put("date",Long.toString(atStartOfDay().getTime()));
            user.setAdditionalDetails(additionalDetails);
        }

        User saved = userService.save(user);
        if(saved == null) {
            throw new RuntimeException("User creation failed");
        }else{
            UserCredentials credentials = userService.findCredentialsByUserId(saved.getId());
            credentials.setPassword("password");
            UserCredentials savedCred = userService.saveUserCredentials(credentials);
            if(savedCred == null){
                throw new RuntimeException("User creation failed");
            }
            return saved;
        }
    }

    private Date atStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = Date.from(Instant.now());
        long currentDateTime = currentDate.getTime();
        calendar.setTimeInMillis(currentDateTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void deleteUser(UUID userId) {
        UserCredentials credentials = userService.findCredentialsByUserId(userId);
        if(credentials != null)
            userService.deleteUserCredentialsById(credentials.getId());
        userService.deleteById(UUIDConverter.fromTimeUUID(userId));
    }
}
