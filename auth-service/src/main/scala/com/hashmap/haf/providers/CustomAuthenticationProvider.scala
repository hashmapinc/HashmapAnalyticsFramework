package com.hashmap.haf.providers

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class CustomAuthenticationProvider extends AuthenticationProvider{
	override def authenticate(authentication: Authentication): Authentication = ???

	override def supports(aClass: Class[_]): Boolean = ???
}
