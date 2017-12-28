package com.hashmap.haf.services;

import com.hashmap.haf.models.UserInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "users.provider", havingValue = "database")
public class DatabaseUserDetailsService implements UserDetailsService {

    @Override
    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
