package com.hashmap.haf.functions.api.services

import java.io.File
import java.net.URI
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.api.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.models.IgniteFunctionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FileSystemDiscoveryService(@Autowired gateway: FunctionsDiscoveryGateway)
	extends FunctionsDiscoveryService{

	override def discoverFunctions(uri: URI): Unit = {
		val files = gateway.readFrom(uri)
		val detector = ProcessorFactory[IgniteFunction, IgniteFunctionType]
		detector.process(files.filter(isJar))
	}

	private def isJar(f: File) = {
		f.getPath.endsWith(".jar")
	}
}
