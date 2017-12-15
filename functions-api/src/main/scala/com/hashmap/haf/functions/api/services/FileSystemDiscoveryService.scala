package com.hashmap.haf.functions.api.services

import java.io.File
import java.net.URI

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.api.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.api.processors.SourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FileSystemDiscoveryService @Autowired()(inputGateway: FunctionsDiscoveryGateway,
                                              outputGateway: FunctionsOutputGateway,
                                              sourceGenerator: SourceGenerator[IgniteFunctionType])
	extends FunctionsDiscoveryService{

	override def discoverFunctions(uri: URI): Unit = {
		val files = inputGateway.readFrom(uri)
		val processor = ProcessorFactory[IgniteFunction, IgniteFunctionType]
		processor.process(files.filter(isJar)).foreach(s => {
			val clazzName = s._1
			val function = s._2
			sourceGenerator.generateSource(function, clazzName)
		})

	}

	private def isJar(f: File) = {
		f.getPath.endsWith(".jar")
	}
}
