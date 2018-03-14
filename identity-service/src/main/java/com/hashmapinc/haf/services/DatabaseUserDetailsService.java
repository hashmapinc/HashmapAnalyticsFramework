package com.hashmapinc.haf.services;

import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
//@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired private UsersDao usersDao;

    @Override
    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException {
        return usersDao.findByUserName(s);
    }

    @Override
    public User save(User user) {
        return usersDao.save(user);
    }

    @Override
    public User findById(String id) {
        return usersDao.findById(id);
    }

    @Override
    public Collection<User> findAll() {
        return usersDao.findAll();
    }

    @Override
    public void deleteById(String userId) {
        usersDao.deleteById(userId);
    }
}
