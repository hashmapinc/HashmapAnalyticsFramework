package com.hashmap.haf.functions.deployment

import org.apache.ignite.services.ServiceConfiguration
import org.apache.ignite.{Ignite, IgniteServices, Ignition}

trait DeploymentService {

	def deploy(cfg: ServiceConfiguration)

}

class DefaultDeploymentService(configurationPath: String) extends DeploymentService{

	private val igConfig = getClass.getResource(configurationPath).getPath

	override def deploy(cfg: ServiceConfiguration): Unit = {

		val ignite: Ignite = Ignition.start(igConfig)

		val igServices: IgniteServices = ignite.services

		igServices.deploy(cfg)
	}
}

object DefaultDeploymentService {
	def apply(configurationPath: String): DefaultDeploymentService = new DefaultDeploymentService(configurationPath)

	def apply(): DefaultDeploymentService = new DefaultDeploymentService("/examples/cache.xml")
}