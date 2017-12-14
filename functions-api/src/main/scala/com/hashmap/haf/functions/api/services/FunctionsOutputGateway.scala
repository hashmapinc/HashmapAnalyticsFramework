package com.hashmap.haf.functions.api.services

import java.net.URI

trait FunctionsOutputGateway {
	def writeTo(uri: URI)
}
