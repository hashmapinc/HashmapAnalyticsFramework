package com.hashmap.haf.functions.api.configs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.{DefaultOAuth2ClientContext, OAuth2RestTemplate}
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.{EnableResourceServer, ResourceServerConfigurerAdapter}
import org.springframework.security.oauth2.provider.token.RemoteTokenServices

@EnableResourceServer
@Configuration
class FunctionsResourceServer @Autowired()(sso: ResourceServerProperties) extends ResourceServerConfigurerAdapter{

	@Bean
	@ConfigurationProperties(prefix = "security.oauth2.client")
	def clientCredentialsResourceDetails = new ClientCredentialsResourceDetails

	@Bean
	def oauth2FeignRequestInterceptor: OAuth2FeignRequestInterceptor = {
		new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext, clientCredentialsResourceDetails)
	}

	@Bean
	def clientCredentialsRestTemplate: OAuth2RestTemplate =
		new OAuth2RestTemplate(clientCredentialsResourceDetails)

	@Bean
	def tokenServices = {
		val tokenService = new RemoteTokenServices()
		tokenService.setRestTemplate(clientCredentialsRestTemplate)
		tokenService.setCheckTokenEndpointUrl(sso.getUserInfoUri)
		tokenService.setClientId(clientCredentialsResourceDetails.getClientId)
		tokenService.setClientSecret(clientCredentialsResourceDetails.getClientSecret)
		tokenService
	}

	@throws[Exception]
	override def configure(http: HttpSecurity): Unit = {
		http
			.authorizeRequests
			.antMatchers("/", "/demo").permitAll
			.anyRequest.authenticated
	}
}
