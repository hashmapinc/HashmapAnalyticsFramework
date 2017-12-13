package com.hashmap.haf.functions.api.gateways

import java.io.File
import java.net.URI
import com.hashmap.haf.functions.api.services.FunctionsDiscoveryGateway
import org.springframework.stereotype.Component

@Component
class FileSystemFunctionDiscoveryGateway extends FunctionsDiscoveryGateway{
	override def readFrom(uri: URI): List[File] = ???

	override def writeTo(uri: URI): Unit = ???

	override def poll(uri: URI): Unit = ???
}
