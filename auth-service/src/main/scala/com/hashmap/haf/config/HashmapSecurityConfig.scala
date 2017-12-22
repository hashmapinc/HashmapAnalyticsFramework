package com.hashmap.haf.config

import com.hashmap.haf.providers.CustomAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class HashmapSecurityConfig @Autowired()(customAuthenticationProvider: CustomAuthenticationProvider) extends WebSecurityConfigurerAdapter{

}
