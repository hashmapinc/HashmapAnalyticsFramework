package com.hashmapinc.haf.configs;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.Map;

import static com.hashmapinc.haf.constants.JWTClaimsConstants.CLIENT_ID;

public class ClientResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    public ClientResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory);
    }

    protected ClientResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory, grantType);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = new HashMap<>(tokenRequest.getRequestParameters());
        requestParameters.put(CLIENT_ID, client.getClientId());
        tokenRequest.setRequestParameters(requestParameters);
        return super.getOAuth2Authentication(client, tokenRequest);
    }
}
