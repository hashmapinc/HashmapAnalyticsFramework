package com.hashmap.haf.functions.api.config

import javax.annotation.PostConstruct

import com.hashmap.haf.functions.api.services.FunctionsBootstrapService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(Array("com.hashmap.haf.entities"))
@EnableJpaRepositories(Array("com.hashmap.haf.repository"))
@ComponentScan(Array("com.hashmap.haf"))
@EnableAutoConfiguration
class FunctionApiConfig {

  @Autowired private var context:ApplicationContext = _

  @PostConstruct
  def getFunctionsBootstrapServiceBean: Unit = {
    context.getBean(classOf[FunctionsBootstrapService]).init()
  }
}

