package com.hashmap.haf.functions.api.deployment

import com.hashmap.haf.functions.api.service.ServiceFunction
import org.apache.ignite.{Ignite, IgniteServices, Ignition}

trait DeploymentService {

	def deployNodeSingleton(serviceName: String, function: ServiceFunction)

	def deployClusterSingleton(serviceName: String, function: ServiceFunction)

}

class DefaultDeploymentService(configurationPath: String) extends DeploymentService{

	private val igConfig = getClass.getResource(configurationPath).getPath

	override def deployNodeSingleton(serviceName: String, function: ServiceFunction): Unit = {

		val ignite: Ignite = Ignition.start(igConfig)

		val igServices: IgniteServices = ignite.services

		igServices.deployNodeSingleton(serviceName, function)
	}

	override def deployClusterSingleton(serviceName: String, function: ServiceFunction): Unit = {
		val ignite: Ignite = Ignition.start(igConfig)

		val igServices: IgniteServices = ignite.services

		igServices.deployClusterSingleton(serviceName, function)
	}
}

object DefaultDeploymentService {
	def apply(configurationPath: String): DefaultDeploymentService = new DefaultDeploymentService(configurationPath)

	def apply(): DefaultDeploymentService = new DefaultDeploymentService("/examples/cache.xml")
}