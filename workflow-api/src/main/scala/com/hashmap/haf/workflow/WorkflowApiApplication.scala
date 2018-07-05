package com.hashmap.haf.workflow


import com.hashmap.haf.workflow.install.WorkflowInstallationService
import com.hashmap.haf.workflow.mappers.ScalaObjectMapperBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAutoConfiguration
@ComponentScan
@EnableOAuth2Client
class WorkflowApiApplication

object WorkflowApiApplication extends App{
  private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
  private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "workflow-api"

  if(isInstall(args)) {
    val application = new SpringApplication(classOf[WorkflowApiApplication])
    application.setAdditionalProfiles("install")
    val context = application.run(updateArguments(args): _*)
    context.getBean(classOf[WorkflowInstallationService]).performInstall()
  }else{
    SpringApplication.run(classOf[WorkflowApiApplication], updateArguments(args): _*)
  }


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

  private def isInstall(args: Array[String]): Boolean ={
    val argsAsList = args.toList
    argsAsList.find(_.startsWith("--install")) match {
      case Some(_) => true
      case _ => false
    }
  }
}
