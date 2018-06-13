package com.hashmapinc.haf.services;

import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
//@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired private UsersDao usersDao;

    @Override
    public UserInformation loadUserByUsername(String s, String clientId) throws UsernameNotFoundException {
        return usersDao.findByUserName(s, clientId);
    }

    @Override
    public User save(User user) {
        return usersDao.save(user);
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
}
