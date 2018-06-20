package com.hashmapinc.haf.services;

import com.hashmapinc.haf.dao.UserCredentialsDao;
import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.ActivationType;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserCredentials;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.requests.ActivateUserRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
//@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final int DEFAULT_TOKEN_LENGTH = 30;

    @Autowired private UsersDao usersDao;

    @Autowired private UserCredentialsDao userCredentialsDao;

    @Override
    public UserInformation loadUserByUsername(String s, String clientId) throws UsernameNotFoundException {
        return usersDao.findByUserName(s, clientId);
    }

    @Override
    public User save(User user) {
        boolean isNewUser = user.getId() == null || findById(user.getId()) == null;
        User savedUser  = usersDao.save(user);
        if (isNewUser) {
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setActivationToken(RandomStringUtils.randomAlphanumeric(DEFAULT_TOKEN_LENGTH));
            userCredentials.setUserId(savedUser.getId());
            userCredentialsDao.save(userCredentials);
        }
        return savedUser;
    }

    @Override
    public User findById(UUID id) {
        return usersDao.findById(id);
    }

    @Override
    public Collection<User> findAllByClientId(String clientId) {
        return usersDao.findAllByClientId(clientId);
    }

    @Override
    public void deleteById(String userId) {
        usersDao.deleteById(userId);
    }

    @Override
    public UserCredentials findCredentialsByUserId(UUID userId) {
        return userCredentialsDao.findByUserId(userId);
    }

    @Override
    public UserCredentials findCredentialsByActivationToken(String activationToken) {
        return userCredentialsDao.findByActivationToken(activationToken);
    }

    @Override
    public UserCredentials activateUserCredentials(ActivateUserRequest activateUserRequest) {
        UserCredentials userCredentials = findCredentialsByActivationToken(activateUserRequest.getActivateToken());
        if (userCredentials == null) {
            throw new IllegalArgumentException(String.format("Unable to find user credentials by activateToken [%s]", activateUserRequest.getActivateToken()));
        }

        User user = findById(userCredentials.getUserId());
        if (user.isEnabled()) {
            throw new IllegalStateException("User is already activated");
        }

        userCredentials.setPassword(activateUserRequest.getPassword());
        userCredentials.setActivationToken(null);
        UserCredentials savedUserCreds = userCredentialsDao.save(userCredentials);
        user.setEnabled(true);
        save(user);
        return savedUserCreds;

    }

    @Override
    public UserCredentials saveUserCredentials(UserCredentials credentials) {
        return userCredentialsDao.save(credentials);
    }
}
