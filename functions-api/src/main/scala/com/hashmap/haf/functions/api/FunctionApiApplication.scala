package com.hashmap.haf.functions.api

import com.hashmap.haf.functions.api.services.FunctionsBootstrapService
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.{SpringApplication, SpringBootConfiguration}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client

@EnableDiscoveryClient
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(Array("com.hashmap.haf.repository"))
@EntityScan(Array("com.hashmap.haf.entities"))
@ComponentScan(Array("com.hashmap.haf"))
@EnableOAuth2Client
@EnableFeignClients
@EnableGlobalMethodSecurity(prePostEnabled = true)
class FunctionApiApplication

object FunctionApiApplication extends App{
	private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
	private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "functions-api"

	private val context = SpringApplication.run(classOf[FunctionApiApplication], updateArguments(args): _*)
	context.getBean(classOf[FunctionsBootstrapService]).init()

	private def updateArguments(args: Array[String]): List[String] ={
		val argsAsList = args.toList
		argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
			case Some(_) => argsAsList
			case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
		}
	}
}
