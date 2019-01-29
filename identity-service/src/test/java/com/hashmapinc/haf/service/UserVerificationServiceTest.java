package com.hashmapinc.haf.service;

import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import com.hashmapinc.haf.services.UserVerificationServiceImpl;
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
public class UserVerificationServiceTest {

    private static final String clientId = "tempus";
    private final String TENANT_ADMIN = "TENANT_ADMIN";

    @Autowired
    private DatabaseUserDetailsService userService;

    @Autowired
    private UserVerificationServiceImpl userVerificationService;

    @Autowired
    private UsersDao usersDao;

    @Test
    public void shouldDisableAllExpiredTrialUser() throws Exception{

        createUser("trialUser",NULL_UUID.toString(),TENANT_ADMIN,true);

        List<User> tenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(tenantAdminUsers.size(),1);
        Assert.assertTrue(tenantAdminUsers.get(0).isEnabled());
        Thread.sleep(100); //NOSONAR

        userVerificationService.disableAllExpiredUser(0);

        List<User> foundTenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(foundTenantAdminUsers.size(),1);
        Assert.assertFalse(foundTenantAdminUsers.get(0).isEnabled());

        deleteUser(tenantAdminUsers.get(0).getId());
    }

    @Test
    public void shouldNotDisableNonTrialUser() throws Exception{
        createUser("demo",NULL_UUID.toString(),TENANT_ADMIN,false);

        List<User> tenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(tenantAdminUsers.size(),1);
        Assert.assertTrue(tenantAdminUsers.get(0).isEnabled());
        Thread.sleep(100); //NOSONAR

        userVerificationService.disableAllExpiredUser(0);

        List<User> foundTenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(foundTenantAdminUsers.size(),1);
        Assert.assertTrue(foundTenantAdminUsers.get(0).isEnabled());
    }

    @Test
    public void shouldDisableExpiredTrialTenantCustomersAlso() throws Exception{
        User savedAdminUser = createUser("trialUser",UUID.randomUUID().toString(),TENANT_ADMIN,true);
        String CUSTOMER_USER = "CUSTOMER_USER";
        User savedCustomer = createUser("bob",savedAdminUser.getTenantId(), CUSTOMER_USER,false);

        Thread.sleep(100); //NOSONAR

        userVerificationService.disableAllExpiredUser(0);

        List<User> foundTenantAdminUsers = usersDao.findByAuthorities("TENANT_ADMIN");
        Assert.assertEquals(foundTenantAdminUsers.size(),1);
        Assert.assertFalse(foundTenantAdminUsers.get(0).isEnabled());


        for (User user : foundTenantAdminUsers) {
            List<User> customerUsers = usersDao.findByTenantId(user.getTenantId());
            for (User customer : customerUsers) {
                Assert.assertFalse(customer.isEnabled());
            }
        }

        deleteUser(savedAdminUser.getId());
        deleteUser(savedCustomer.getId());
    }

    @Test
    public void shouldNotDisableNonExpiredTrialUser() throws Exception {
        createUser("trialUser",NULL_UUID.toString(),TENANT_ADMIN,true);

        List<User> tenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(tenantAdminUsers.size(),1);
        Assert.assertTrue(tenantAdminUsers.get(0).isEnabled());
        Thread.sleep(100); //NOSONAR

        userVerificationService.disableAllExpiredUser(1000);

        List<User> foundTenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);
        Assert.assertEquals(foundTenantAdminUsers.size(),1);
        Assert.assertTrue(foundTenantAdminUsers.get(0).isEnabled());

        deleteUser(tenantAdminUsers.get(0).getId());
    }

    private User createUser(String userName , String tenantId , String authority ,boolean trialAccount){

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
            Date currentTime = Date.from(Instant.now());
            long time = currentTime.getTime();
            String DATE = "date";
            additionalDetails.put(DATE,Long.toString(time));
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

    private void deleteUser(UUID userId) {
        UserCredentials credentials = userService.findCredentialsByUserId(userId);
        if(credentials != null)
            userService.deleteUserCredentialsById(credentials.getId());
        userService.deleteById(UUIDConverter.fromTimeUUID(userId));
    }
}
