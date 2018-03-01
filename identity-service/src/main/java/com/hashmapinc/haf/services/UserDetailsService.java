package com.hashmapinc.haf.services;

import com.hashmapinc.haf.models.UserInformation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    UserInformation loadUserByUsername(String s) throws UsernameNotFoundException;

}
