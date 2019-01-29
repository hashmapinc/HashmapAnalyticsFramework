package com.hashmapinc.haf.services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
public class UserPurgeTask {

    @Value("${expiry.time.Minutes}")
    private  int expiryTimeInMinutes;

    @Autowired
    private UserVerificationServiceImpl userVerificationService;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpiredUsers() {
        log.trace("Executing purgeExpiredUsers");
        userVerificationService.disableAllExpiredUser(expiryTimeInMinutes);
    }
}
