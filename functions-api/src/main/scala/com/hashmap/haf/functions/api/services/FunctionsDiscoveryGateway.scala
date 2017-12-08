package com.hashmap.haf.functions.api.services

import java.io.File
import java.net.URI

trait FunctionsDiscoveryGateway {

	def readFrom(uri: URI): List[File]

	def writeTo(uri: URI)

	def poll(uri: URI): Unit

}
