package com.hashmap.haf.providers;

import org.springframework.security.authentication.AuthenticationProvider;

public abstract class CustomAuthenticationProvider implements AuthenticationProvider{

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

}
