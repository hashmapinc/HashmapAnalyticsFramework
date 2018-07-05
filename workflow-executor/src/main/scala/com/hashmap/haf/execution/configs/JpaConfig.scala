package com.hashmap.haf.execution.configs

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(Array("com.hashmap.haf.repository", "com.hashmap.haf.workflow.dao", "com.hashmap.haf.execution.events"))
@EntityScan(Array("com.hashmap.haf.entities", "com.hashmap.haf.workflow.entity", "com.hashmap.haf.execution.events.entity"))
@ComponentScan(Array("com.hashmap.haf.execution.events"))
@EnableTransactionManagement
class JpaConfig {

}
