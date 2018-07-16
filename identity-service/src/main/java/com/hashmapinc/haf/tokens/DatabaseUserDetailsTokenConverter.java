package com.hashmapinc.haf.tokens;

import com.hashmapinc.haf.models.SecurityUser;
import com.hashmapinc.haf.models.User;
import com.hashmapinc.haf.models.UserInformation;
import com.hashmapinc.haf.services.DatabaseUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.hashmapinc.haf.constants.JWTClaimsConstants.*;

@Component
@ConditionalOnProperty(value = "security.provider", havingValue = "oauth2-local")
public class DatabaseUserDetailsTokenConverter implements UserAuthenticationConverter{

    private final DatabaseUserDetailsService userDetailsService;

    @Autowired
    public DatabaseUserDetailsTokenConverter(final DatabaseUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        User userInfo = (User)user.getUser();
        response.put(USER_NAME, userInfo.getUserName());
        response.put(ID, userInfo.getId());
        response.put(FIRST_NAME, userInfo.getFirstName());
        response.put(LAST_NAME, userInfo.getLastName());
        response.put(TENANT_ID, userInfo.getTenantId());
        response.put(CUSTOMER_ID, userInfo.getCustomerId());
        response.put(CLIENT_ID, userInfo.getClientId());
        response.put(ENABLED, user.isEnabled());
        response.putAll(userInfo.getAdditionalDetails());
        if(authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        if(user.getUser().getPermissions() != null && !user.getUser().getPermissions().isEmpty()) {
            response.put(PERMISSIONS, new HashSet<>(user.getUser().getPermissions()));
        }

        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if(map.containsKey(USER_NAME)) {
            Object principal = map.get(USER_NAME);
            UserInformation user = userDetailsService.loadUserByUsername((String) principal, (String) map.get(CLIENT_ID));

            if(user != null) {
                SecurityUser securityUser = new SecurityUser(user, user.isEnabled());
                return new UsernamePasswordAuthenticationToken(securityUser, "N/A", securityUser.getAuthorities());
            }
        }
        return null;
    }
}
