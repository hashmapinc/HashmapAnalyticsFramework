package com.hashmap.haf.functions.services

import java.net.URI

trait FunctionsDiscoveryService {

	def discoverFunctions(uri: URI)

	def loadClazz(name: String): Option[Class[_]]

}
