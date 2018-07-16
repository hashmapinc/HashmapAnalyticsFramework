package com.hashmapinc.haf.services;

import com.hashmapinc.haf.configs.ClientConfig;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.HashMap;

public class PropertiesClientUserDetailsService implements ClientDetailsService{

    private HashMap<String, ClientDetails> clients = new HashMap<>();

    public PropertiesClientUserDetailsService(ClientConfig config){
        config.getClients().forEach((k, v) -> {
            try {
                BaseClientDetails details = new BaseClientDetails();
                details.setClientId(k);
                details.setClientSecret(v.getClientSecret());
                details.setAuthorizedGrantTypes(v.getGrantTypes());
                details.setScope(v.getScopes());
                clients.put(k, details);
            } catch (Exception e) {
                throw new ClientRegistrationException("Error while reading clients from configurations", e);
            }
        });
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId){
        ClientDetails details = clients.get(clientId);
        if(details == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        } else {
            return details;
        }
    }
}
