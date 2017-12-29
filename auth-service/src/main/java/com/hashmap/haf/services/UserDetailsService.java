package com.hashmap.haf.services;

import com.hashmap.haf.models.UserInformation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    public UserInformation loadUserByUsername(String s) throws UsernameNotFoundException;

}
