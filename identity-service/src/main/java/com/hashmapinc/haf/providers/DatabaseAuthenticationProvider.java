package com.hashmapinc.haf.providers;


import com.hashmapinc.haf.models.SecurityUser;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.util.Arrays;

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

        if (!(authentication instanceof UsernamePasswordAuthenticationToken) &&
                !(authentication instanceof PreAuthenticatedAuthenticationToken)) {
            throw new BadCredentialsException("Authentication Failed. Bad user principal.");
        }

        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = (String) authentication.getPrincipal();
            String password = (String) authentication.getCredentials();
            return authenticateByUsernameAndPassword(username, password);
        }else{
            String username = (String)((UsernamePasswordAuthenticationToken)authentication.getPrincipal()).getPrincipal();
            PreAuthenticatedAuthenticationToken auth  = (PreAuthenticatedAuthenticationToken)authentication;
            return reAuthenticateWithUsername(username, auth);
        }
    }

    protected Authentication authenticateByUsernameAndPassword(String username, String password) {
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

        SecurityUser securityUser = new SecurityUser(userInfo, userInfo.isEnabled());

        return new UsernamePasswordAuthenticationToken(username, password, securityUser.getAuthorities());
    }

    protected Authentication reAuthenticateWithUsername(String username, PreAuthenticatedAuthenticationToken auth){
        UserInformation userInfo = userDetailsService.loadUserByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (!userInfo.isEnabled()) {
            throw new DisabledException("User is not active");
        }

        if (userInfo.getAuthorities() == null || userInfo.getAuthorities().isEmpty())
            throw new InsufficientAuthenticationException("User has no authority assigned");

        PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(userInfo.getUserName(), auth.getCredentials(), auth.getAuthorities());
        result.setDetails(auth.getDetails());

        return result;
    }

    private UserInformation dummyUser(){
        User u = new User("tempus_user");
        u.setEnabled(true);
        u.setFirstName("jay");
        u.setUserName("jay");
        u.setPassword("jay");
        u.setTenantId("123");
        u.setAuthorities(Arrays.asList("admin"));
        return  u;
    }
}
