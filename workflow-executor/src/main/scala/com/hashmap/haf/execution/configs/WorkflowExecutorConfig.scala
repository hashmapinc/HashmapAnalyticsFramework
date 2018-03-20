package com.hashmap.haf.execution.configs

import org.apache.ignite.internal.IgnitionEx
import org.apache.ignite.{Ignite, Ignition}
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ComponentScan(Array("com.hashmap.haf"))
@EnableJpaRepositories(Array("com.hashmap.haf.repository", "com.hashmap.haf.workflow.dao"))
@EntityScan(Array("com.hashmap.haf.entities", "com.hashmap.haf.workflow.entity"))
class WorkflowExecutorConfig {

	@Value("${functions.ignite.config}")
	var igniteConfigPath: String = _

	@Bean(destroyMethod = "close")
	def ignite(): Ignite = {
		val igConfig = Thread.currentThread().getContextClassLoader.getResource(igniteConfigPath)
		val configuration = IgnitionEx.loadConfiguration(igConfig).get1()
		configuration.setClientMode(true)
		Ignition.start(configuration)
	}
}
