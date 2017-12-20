package com.hashmap.haf.functions.api.services

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.FunctionsInputGateway
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.AnnotationsProcessor
import com.hashmap.haf.functions.services.AbstractFunctionsDiscoveryService
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FileSystemPersistentDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                                        igniteFunctionService: IgniteFunctionTypeService)
	extends AbstractFunctionsDiscoveryService(inputGateway){

	override type T = IgniteFunction
	override type R = IgniteFunctionType

	override protected def processFunction(r: IgniteFunctionType): Unit = igniteFunctionService.save(r)

	override protected def newListener(): FunctionsChangeListener = ???

	override protected def newProcessor: AnnotationsProcessor[IgniteFunction, IgniteFunctionType] = ProcessorFactory[IgniteFunction, IgniteFunctionType]

	override protected def serviceNameFunction(r: IgniteFunctionType): String = r.getService
}
