package com.hashmapinc.haf.dao;

import com.hashmapinc.haf.models.UserCredentials;

import java.util.UUID;

public interface UserCredentialsDao {

    UserCredentials save(UserCredentials userCredentials);

    UserCredentials findByUserId(UUID userId);

    UserCredentials findByActivationToken(String activationToken);

    UserCredentials findByResetToken(String resetToken);
}
