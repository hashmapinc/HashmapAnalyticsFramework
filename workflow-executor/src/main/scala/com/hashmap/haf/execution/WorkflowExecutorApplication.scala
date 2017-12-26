package com.hashmap.haf.execution

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAutoConfiguration
@ComponentScan
class WorkflowExecutorApplication

object WorkflowExecutorApplication extends App {
  private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
  private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "workflow-executor"

  SpringApplication.run(classOf[WorkflowExecutorApplication], updateArguments(args): _*)

  private def updateArguments(args: Array[String]): List[String] ={
    val argsAsList = args.toList
    argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
      case Some(_) => argsAsList
      case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
    }
  }
}