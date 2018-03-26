package com.hashmap.haf.workflow


import com.hashmap.haf.workflow.install.WorkflowInstallationService
import com.hashmap.haf.workflow.mappers.ScalaObjectMapperBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.{Bean, ComponentScan}

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAutoConfiguration
class WorkflowApiApplication

object WorkflowApiApplication extends App{
  private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
  private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "workflow-api"

  val context = SpringApplication.run(classOf[WorkflowApiApplication], updateArguments(args): _*)
  context.getBean(classOf[WorkflowInstallationService]).performInstall()


  @Bean
  def jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer ={
    new ScalaObjectMapperBuilder()
  }

  private def updateArguments(args: Array[String]): List[String] ={
    val argsAsList = args.toList
    argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
      case Some(_) => argsAsList
      case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
    }
  }
}
