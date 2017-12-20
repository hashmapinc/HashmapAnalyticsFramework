package com.hashmap.haf.functions.services

import java.io.File
import java.lang.annotation.Annotation
import java.net.URI
import java.nio.file.Path
import com.hashmap.haf.functions.deployment.DeploymentService
import com.hashmap.haf.functions.gateways.FunctionsInputGateway
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.AnnotationsProcessor
import org.apache.ignite.services.ServiceConfiguration
import scala.util.{Failure, Success, Try}

abstract class AbstractFunctionsDiscoveryService(inputGateway: FunctionsInputGateway) extends FunctionsDiscoveryService{

	type T <: Annotation
	type R

	protected var deploymentService: DeploymentService = _

	override def discoverFunctions(uri: URI): Unit = {
		val files = inputGateway.listFilesFrom(uri)
		process(uri, files)
		inputGateway.poll(uri, newListener())
	}

	def process(uri: URI, files: List[Path]): Unit ={
		files.filter(isJar).foreach{ p =>
			inputGateway.readFileFrom(p.toUri).foreach{ f =>
				processAnnotations(newProcessor, f)
			}
		}
	}

	protected def processAnnotations(processor: AnnotationsProcessor[T, R],
	                                                     f: File): Unit ={
		if(isJar(f.toPath)) {
			processor.detect(f).foreach { case (serviceName, r) =>
				deployService(serviceName, serviceNameFunction(r))
				processFunction(r)
			}
		}
	}

	protected def processFunction(r: R): Unit

	protected def deployService(clazz: String, serviceName: String): Unit ={
		Try(Class.forName(clazz)) match {
			case Success(c) =>
				val instance = c.newInstance().asInstanceOf[ServiceFunction]
				//TODO: get configs from annotations
				val cfg = new ServiceConfiguration()
				cfg.setName(serviceName)
				cfg.setTotalCount(1)
				cfg.setService(instance)
				deploymentService.deploy(cfg)

			case Failure(e) => throw new IllegalStateException("Error while deploying service", e)
		}
	}

	protected def isJar(f: Path): Boolean = {
		f.endsWith(".jar")
	}

	protected def newListener(): FunctionsChangeListener

	protected def newProcessor: AnnotationsProcessor[T, R]

	protected def serviceNameFunction(r: R): String
}
