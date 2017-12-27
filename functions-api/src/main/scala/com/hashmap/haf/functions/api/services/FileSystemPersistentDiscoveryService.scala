package com.hashmap.haf.functions.api.services

import java.io.File
import javax.annotation.PostConstruct
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.deployment.DefaultDeploymentService
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.FunctionsInputGateway
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.AnnotationsProcessor
import com.hashmap.haf.functions.services.AbstractFunctionsDiscoveryService
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service("discoveryService")
class FileSystemPersistentDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                                        igniteFunctionService: IgniteFunctionTypeService)
	extends AbstractFunctionsDiscoveryService(inputGateway){

	override type T = IgniteFunction
	override type R = IgniteFunctionType

	@Value("${functions.ignite.config}")
	var igniteConfig: String = _

	@PostConstruct
	def init(): Unit ={
		Option(igniteConfig) match {
			case Some(c) => deploymentService = DefaultDeploymentService(c)
			case _ => deploymentService = DefaultDeploymentService()
		}
	}

	override protected def processFunction(r: IgniteFunctionType): Unit = igniteFunctionService.save(r)

	override protected def newListener(): FunctionsChangeListener = new FileSystemListener

	override protected def newProcessor: AnnotationsProcessor[IgniteFunction, IgniteFunctionType] =
		ProcessorFactory[IgniteFunction, IgniteFunctionType]

	override protected def serviceNameFunction(r: IgniteFunctionType): String = r.getService

	class FileSystemListener extends FileAlterationListenerAdaptor with FunctionsChangeListener{
		override def onFileCreate(file: File): Unit = {
			processAnnotations(newProcessor, file)
		}

		override def onFileChange(file: File): Unit = {
			processAnnotations(newProcessor, file)
		}

		override def onFileDelete(file: File): Unit = {
		}
	}
}
