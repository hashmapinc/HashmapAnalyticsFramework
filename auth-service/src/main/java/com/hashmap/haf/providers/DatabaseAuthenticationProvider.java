package com.hashmap.haf.providers;

import com.hashmap.haf.models.SecurityUser;
import com.hashmap.haf.models.UserInformation;
import com.hashmap.haf.models.UserPrincipal;
import com.hashmap.haf.services.DatabaseUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.naming.NamingException;

@Component
@ConditionalOnProperty(value = "security.client", havingValue = "oauth2-local")
public class DatabaseAuthenticationProvider extends CustomAuthenticationProvider{

    private final DatabaseUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public DatabaseAuthenticationProvider(final DatabaseUserDetailsService userDetailsService, final BCryptPasswordEncoder encoder) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new BadCredentialsException("Authentication Failed. Bad user principal.");
        }

        UserPrincipal userPrincipal =  (UserPrincipal) principal;
        String username = userPrincipal.getValue();
        String password = (String) authentication.getCredentials();
        return authenticateByUsernameAndPassword(userPrincipal, username, password);
    }

    public Authentication authenticateByUsernameAndPassword(UserPrincipal userPrincipal, String username, String password) {
        UserInformation userInfo = userDetailsService.loadUserByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (!userInfo.isEnabled()) {
            throw new DisabledException("User is not active");
        }

        if (!encoder.matches(password, userInfo.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }
        if (userInfo.getAuthorities() == null || userInfo.getAuthorities().isEmpty())
            throw new InsufficientAuthenticationException("User has no authority assigned");

        SecurityUser securityUser = new SecurityUser(userInfo, userInfo.isEnabled(), userPrincipal);

        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }
}
