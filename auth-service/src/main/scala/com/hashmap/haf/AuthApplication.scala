package com.hashmap.haf

import org.springframework.boot.{SpringApplication, SpringBootConfiguration}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan

@EnableDiscoveryClient
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
class AuthApplication

object AuthApplication extends App{
	SpringApplication.run(classOf[AuthApplication], args: _*)
}
