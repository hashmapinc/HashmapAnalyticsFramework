package com.hashmap.haf.functions.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(Array("com.hashmap.haf.functions.api.service"))
class FunctionApiApplication

object FunctionApiApplication extends App{
	SpringApplication.run(classOf[FunctionApiApplication])
}
