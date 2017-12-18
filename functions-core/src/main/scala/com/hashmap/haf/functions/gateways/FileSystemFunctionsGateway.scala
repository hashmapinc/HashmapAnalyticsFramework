package com.hashmap.haf.functions.gateways

import java.io.File
import java.net.URI
import java.nio.file.Path
import org.springframework.stereotype.Component

@Component
class FileSystemFunctionsGateway extends FunctionsInputGateway with FunctionsOutputGateway{
	override def listFilesFrom(uri: URI): List[Path] = ???

	override def poll(uri: URI): Unit = ???

	override def writeTo(uri: URI, data: Array[Byte]): Unit = ???

	override def readFileFrom(uri: URI): Option[File] = ???
}
