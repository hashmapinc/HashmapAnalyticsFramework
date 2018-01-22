package com.hashmap.haf.scheduler

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAutoConfiguration
class SchedulerApplication

object SchedulerApplication extends App{
	private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
	private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "scheduler"


	SpringApplication.run(classOf[SchedulerApplication], updateArguments(args): _*)


	private def updateArguments(args: Array[String]): List[String] ={
		val argsAsList = args.toList
		argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
			case Some(_) => argsAsList
			case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
		}
	}

}