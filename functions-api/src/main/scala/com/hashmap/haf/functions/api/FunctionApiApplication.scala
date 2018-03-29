package com.hashmap.haf.functions.api

import com.hashmap.haf.functions.api.mappers.ScalaObjectMapperBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean

@EnableDiscoveryClient
@SpringBootApplication
@EnableAutoConfiguration
class FunctionApiApplication {
	@Bean
	def jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer ={
		new ScalaObjectMapperBuilder()
	}
}

object FunctionApiApplication extends App{
	private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
	private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "functions-api"

	SpringApplication.run(classOf[FunctionApiApplication], updateArguments(args): _*)

	private def updateArguments(args: Array[String]): List[String] ={
		val argsAsList = args.toList
		argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
			case Some(_) => argsAsList
			case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
		}
	}
}


