package com.hashmapinc.haf.services;

import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserVerificationServiceImpl implements UserVerificationService{
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsersDao usersDao;

    private final String IS_TRIAL_ACCOUNT = "trialAccount";
    private final String DATE = "date";
    private static final String TENANT_ADMIN = "TENANT_ADMIN";


    @Override
    public void disableAllExpiredUser(final int expiryTimeInMinutes) {
        try {
            List<User> tenantAdminUsers = usersDao.findByAuthorities(TENANT_ADMIN);

            for (User user : tenantAdminUsers) {
                Map<String, String> additionalDetails = user.getAdditionalDetails();
                Boolean trialUser = getTrialUser(additionalDetails);
                Long registeredTime = getRegisteredTime(additionalDetails);

                if(trialUser != null && registeredTime != null) {
                    if (trialUser.equals(true) && isUserExpired(registeredTime,expiryTimeInMinutes)) {
                        user.setEnabled(false);
                        userDetailsService.save(user);
                        disableExpiredTenantCustomers(user.getTenantId());
                    }
                }
            }
        }catch (Exception exp) {
            log.info("scheduler is failed to expiring the trial user");
            log.warn("Exception while expiring the trial user [{}]", exp.getMessage());
        }
    }

    private Long getRegisteredTime(Map<String, String> additionalDetails) {
        Long registeredTime = null;
        if(additionalDetails.get(DATE) != null)
            registeredTime = Long.parseLong(additionalDetails.get(DATE));
        return registeredTime;
    }

    private Boolean getTrialUser(Map<String, String> additionalDetails) {
        Boolean trialUser = null;
        if(additionalDetails.get(IS_TRIAL_ACCOUNT) != null)
            trialUser = Boolean.parseBoolean(additionalDetails.get(IS_TRIAL_ACCOUNT));
        return trialUser;
    }

    private void disableExpiredTenantCustomers(String tenantId) {
        List<User> customerUsers = usersDao.findByTenantId(tenantId);
        for (User user : customerUsers) {
            user.setEnabled(false);
            userDetailsService.save(user);
        }
    }

    private boolean isUserExpired(long registeredTime, final int expiryTimeInMinutes){
        Date expiryDate = calculateExpiryDate(registeredTime,expiryTimeInMinutes);
        Calendar cal = Calendar.getInstance();
        return (expiryDate.getTime() - cal.getTime().getTime()) <= 0;
    }

    private Date calculateExpiryDate(long registeredTime, final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(registeredTime);
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
