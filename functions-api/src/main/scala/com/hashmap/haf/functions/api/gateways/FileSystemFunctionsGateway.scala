package com.hashmap.haf.functions.api.gateways

import java.io.File
import java.net.URI
import com.hashmap.haf.functions.api.services.{FunctionsDiscoveryGateway, FunctionsOutputGateway}
import org.springframework.stereotype.Component

@Component
class FileSystemFunctionsGateway extends FunctionsDiscoveryGateway with FunctionsOutputGateway{
	override def readFrom(uri: URI): List[File] = ???

	override def poll(uri: URI): Unit = ???

	override def writeTo(uri: URI): Unit = ???
}
