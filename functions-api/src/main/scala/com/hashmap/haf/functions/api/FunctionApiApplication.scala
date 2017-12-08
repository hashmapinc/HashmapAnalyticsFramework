package com.hashmap.haf.functions.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(Array("com.hashmap.haf.functions.api.services"))
class FunctionApiApplication

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
