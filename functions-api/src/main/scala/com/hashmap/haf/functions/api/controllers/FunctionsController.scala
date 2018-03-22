package com.hashmap.haf.functions.api.controllers

import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RestController}

import collection.JavaConverters._

@RestController
@RequestMapping(path = Array("/api"))
class FunctionsController @Autowired()(igniteFunctionService: IgniteFunctionTypeService){

	private val logger = LoggerFactory.getLogger(classOf[FunctionsController])

	@GetMapping(path = Array("/functions"))
	def getAllFunctions: List[IgniteFunctionType] = {
		logger.trace("Executing getAllFunctions ")
		igniteFunctionService.findAll().asScala.toList
	}

	@GetMapping(path = Array("/functions/{clazz}"))
	def getFunction(@PathVariable clazz: String): IgniteFunctionType = {
		logger.trace("Executing getFunction for class {}", clazz)
		igniteFunctionService.findByClazz(clazz)
	}

}
