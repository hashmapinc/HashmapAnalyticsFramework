package com.hashmap.haf.functions.api.services

import java.net.URI
import com.hashmap.haf.functions.services.FunctionsDiscoveryService
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class FunctionsBootstrapService @Autowired()(discoveryService: FunctionsDiscoveryService){

	@Value("${functions.input.location}")
	private var functionInputLocation: String = _

	def init(): Unit = {
		//TODO: Validate specified URI
		discoveryService.discoverFunctions(new URI(functionInputLocation))
	}

}
