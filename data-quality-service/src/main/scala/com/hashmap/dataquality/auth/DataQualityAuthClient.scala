package com.hashmap.dataquality.auth

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client

@Configuration
@EnableOAuth2Client
class DataQualityAuthClient {
  @Bean
  @ConfigurationProperties(prefix = "security.oauth2.client") def resourceDetails = new ClientCredentialsResourceDetails

  @Bean
  @Qualifier("oauth2RestTemplate") def restTemplate = new OAuth2RestTemplate(resourceDetails)
}
