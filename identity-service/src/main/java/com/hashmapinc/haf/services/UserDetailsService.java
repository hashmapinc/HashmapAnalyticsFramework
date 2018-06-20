package com.hashmapinc.haf.services;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.requests.ActivateUserRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.UUID;

public interface UserDetailsService {

    UserInformation loadUserByUsername(String s, String clientId) throws UsernameNotFoundException;

    User save(User user);

    User findById(UUID id);

    Collection<User> findAllByClientId(String clientId);

    void deleteById(String userId);

    UserCredentials findCredentialsByUserId(UUID userId);

    UserCredentials findCredentialsByActivationToken(String activationToken);

    UserCredentials activateUserCredentials(ActivateUserRequest activateUserRequest);

    UserCredentials saveUserCredentials(UserCredentials credentials);

}
