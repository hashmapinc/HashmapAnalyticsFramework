package com.hashmapinc.haf.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.List;

@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter{

    @Autowired private JwtSettings settings;

    @Autowired private ClientConfig config;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        DefaultTokenServices tokenServices = tokenService();
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(accessTokenConverter());
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .tokenEnhancer(accessTokenConverter())
                .tokenServices(tokenServices);
        //TokenGranter can be customized to Generate new type of token
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        config.getClients().forEach((k, v) -> {
            try {
                clients
                        .inMemory()
                        .withClient(k)
                        .secret(v.getClientSecret())
                        .authorizedGrantTypes(listToArray(v.getGrantTypes()))
                        .scopes(listToArray(v.getScopes()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String[] listToArray(List<String> l){
        return l.toArray(new String[l.size()]);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(settings.getTokenSigningKey());
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService(){
        DefaultTokenServices service = new DefaultTokenServices();
        service.setTokenStore(tokenStore());
        service.setSupportRefreshToken(true);
        service.setAuthenticationManager(authenticationManager);
        service.setRefreshTokenValiditySeconds(settings.getRefreshTokenExpTime());
        service.setAccessTokenValiditySeconds(settings.getTokenExpirationTime());
        return service;
    }
}
