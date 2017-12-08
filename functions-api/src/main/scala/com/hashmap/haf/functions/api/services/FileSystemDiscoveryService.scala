package com.hashmap.haf.functions.api.services

import java.io.File
import java.net.URI

import org.springframework.beans.factory.annotation.Autowired

class FileSystemDiscoveryService(@Autowired gateway: FunctionsDiscoveryGateway)
	extends FunctionsDiscoveryService{

	/*@Value("${functions.location}") var location : String = _
	/*@Autowired
	def this(@Value("${functions.location}") location : String,
	         gateway: FunctionsDiscoveryGateway) {
		this()
		discoverFunctions(new URI(location))
	}*/*/

	override def discoverFunctions(uri: URI): Unit = {
		val files = gateway.readFrom(uri)
		files.foreach(f => {
			if(isJar(f)){

			}
		})
	}

	private def isJar(f: File) = {
		f.getPath.endsWith(".jar")
	}
}
