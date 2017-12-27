package com.hashmap.haf.services;

import com.hashmap.haf.models.UserInformation;
import com.hashmap.haf.providers.DatabaseAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(DatabaseAuthenticationProvider.class)
public class DatabaseUserDetailsService implements UserDetailsService {

    @Override
    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
