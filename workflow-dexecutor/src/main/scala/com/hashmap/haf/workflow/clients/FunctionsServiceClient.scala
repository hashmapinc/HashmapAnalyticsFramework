package com.hashmap.haf.workflow.clients

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.{GetMapping, PathVariable}

@FeignClient(name = "functions-service")
trait FunctionsServiceClient {

	//TODO: Change return type to IgniteFunctionType
	@GetMapping(path = Array("/functions/{clazz}"))
	def getFunction(@PathVariable("clazz") functionClazz: String): String
}
