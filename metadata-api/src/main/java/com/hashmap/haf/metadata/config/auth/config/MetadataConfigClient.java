package com.hashmap.haf.metadata.config.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MetadataConfigClient {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails resourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new OAuth2RestTemplate(resourceDetails());
    }

    /**
     * Add following to rest controller
     *
     * @Autowire RestTemplate
     *
     * to each controller
     * restTemplate.getForObject("/uri", classOf[String]);
     *
     * uri : http://localhost:9090/api/metaconfig
     */


}
