package com.hashmap.haf.services;

import com.hashmap.haf.dao.UsersDao;
import com.hashmap.haf.models.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired private UsersDao usersDao;

    @Override
    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException {
        return usersDao.findByUserName(s);
    }
}
