package com.hashmapinc.haf.tokens;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.LinkedHashMap;
import java.util.Map;

public class IssuedAtTokenEnhancer implements TokenEnhancer{
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        Map<String, Object> claims = new LinkedHashMap<>();

        if(additionalInformation != null){
            claims.putAll(additionalInformation);
        }

        claims.put("iat", System.currentTimeMillis() / 1000);

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(claims);

        return accessToken;
    }
}
