package com.hashmapinc.haf.services;

import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public interface UserDetailsService {

    UserInformation loadUserByUsername(String s) throws UsernameNotFoundException;

    User save(User user);

    User findById(String id);

    Collection<User> findAll();

}
