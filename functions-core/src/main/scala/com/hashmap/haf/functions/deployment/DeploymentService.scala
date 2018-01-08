package com.hashmap.haf.functions.deployment

import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.lang.IgnitePredicate
import org.apache.ignite.services.ServiceConfiguration
import org.apache.ignite.{Ignite, IgniteServices, Ignition}

trait DeploymentService {

	def deploy(cfg: ServiceConfiguration)

}

class DefaultDeploymentService(configurationPath: String) extends DeploymentService{

	private val igConfig = getClass.getResource(configurationPath).toURI.toURL
	private val ignite: Ignite = Ignition.start(igConfig)

	override def deploy(cfg: ServiceConfiguration): Unit = {
		cfg.setNodeFilter(new ServiceFilter())
		val igServices: IgniteServices = ignite.services(ignite.cluster().forServers())

		igServices.deploy(cfg)
	}

	class ServiceFilter extends IgnitePredicate[ClusterNode] {
		override def apply(node: ClusterNode): Boolean = { // The service will be deployed on non client nodes
			!node.isClient
		}
	}
}

object DefaultDeploymentService {
	def apply(configurationPath: String): DefaultDeploymentService = new DefaultDeploymentService(configurationPath)

	def apply(): DefaultDeploymentService = new DefaultDeploymentService("/examples/cache.xml")
}