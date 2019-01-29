package com.hashmapinc.haf.services;

public interface UserVerificationService {
     void disableAllExpiredUser(final int expiryTimeInMinutes);
}
