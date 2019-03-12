package com.hashmap.dataquality

import com.hashmap.dataquality.service.StreamsApp
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.{ComponentScan, Configuration, PropertySource}

@EnableAutoConfiguration
@Configuration
@PropertySource(Array("classpath:data-quality-service.yml"))
@ComponentScan(Array("com.hashmap.dataquality"))
class DataQualityServiceApplication

object DataQualityServiceApplication extends App {

  private val SPRING_CONFIG_NAME_KEY = "--spring.config.name"
  private val DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "data-quality-service"

  private val context = SpringApplication.run(classOf[DataQualityServiceApplication], updateArguments(args): _*)

  context.getBean(classOf[StreamsApp]).run()

  private def updateArguments(args: Array[String]): List[String] = {
    val argsAsList = args.toList
    argsAsList.find(_.startsWith(SPRING_CONFIG_NAME_KEY)) match {
      case Some(_) => argsAsList
      case _ => DEFAULT_SPRING_CONFIG_PARAM :: argsAsList
    }
  }

}