package com.hashmapinc.haf.providers;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public abstract class CustomAuthenticationProvider implements AuthenticationProvider{

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)) ||
                (PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
