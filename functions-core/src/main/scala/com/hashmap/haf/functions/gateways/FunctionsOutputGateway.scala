package com.hashmap.haf.functions.gateways

import java.net.URI

trait FunctionsOutputGateway {
	def writeTo(uri: URI, data: Array[Byte]): Unit
}
