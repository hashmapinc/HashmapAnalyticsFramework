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
        Map<String, Object> response = new LinkedHashMap();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        User userInfo = (User)user.getUser();
        response.put("user_name", userInfo.getUserName());
        response.put("id", userInfo.getId());
        response.put("firstName", userInfo.getFirstName());
        response.put("lastName", userInfo.getLastName());
        response.put("tenant_id", userInfo.getTenantId());
        response.put("customer_id", userInfo.getCustomerId());
        response.put("client_id", userInfo.getClientId());
        response.put("enabled", user.isEnabled());
        response.putAll(userInfo.getAdditionalDetails());
        if(authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        if(user.getUser().getPermissions() != null && !user.getUser().getPermissions().isEmpty()) {
            response.put("permissions", new HashSet(user.getUser().getPermissions()));
        }

        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if(map.containsKey("user_name")) {
            Object principal = map.get("user_name");
            //TODO: Make sure to get the User using tenant_id as well
            UserInformation user = userDetailsService.loadUserByUsername((String) principal, (String) map.get("client_id"));

            if(user != null) {
                SecurityUser securityUser = new SecurityUser(user, user.isEnabled());
                return new UsernamePasswordAuthenticationToken(securityUser, "N/A", securityUser.getAuthorities());
            }
        }
        return null;
    }
}
