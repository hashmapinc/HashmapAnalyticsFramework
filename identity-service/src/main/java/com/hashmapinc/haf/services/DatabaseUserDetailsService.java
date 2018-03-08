package com.hashmapinc.haf.services;

import com.hashmapinc.haf.dao.UsersDao;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired private UsersDao usersDao;

    @Override
    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException {
        return dummyUser();//usersDao.findByUserName(s);
    }

    @Override
    public User save(User user) {
        return usersDao.save(user);
    }

    private UserInformation dummyUser(){
        User u = new User("tempus_user");
        u.setEnabled(true);
        u.setFirstName("jay");
        u.setUserName("jay");
        u.setPassword("$2a$10$az45PJqtLRKTFWXgH5CImuB3Xdrzn1RVsh7GcfCeBoPh/4via30uO");
        u.setTenantId("123");
        u.setAuthorities(Arrays.asList("admin"));
        return  u;
    }
}
