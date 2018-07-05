package com.hashmap.haf.functions.api.controllers

import java.security.Principal

import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.service.IgniteFunctionTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RestController}
import collection.JavaConverters._

@RestController
@RequestMapping(path = Array("/api"))
class FunctionsController @Autowired()(igniteFunctionService: IgniteFunctionTypeService){

	@PreAuthorize("hasAuthority('admin')")
	@GetMapping(path = Array("/functions"))
	def getAllFunctions: List[IgniteFunctionType] = {
		igniteFunctionService.findAll().asScala.toList
	}

	@PreAuthorize("#oauth2.hasScope('ui') or #oauth2.hasScope('server')")
	@GetMapping(path = Array("/users/current"))
	def getCurrent(principal: Principal): Principal = {
		principal
	}

	@GetMapping(path = Array("/functions/{clazz}"))
	def getFunction(@PathVariable clazz: String): IgniteFunctionType = {
		igniteFunctionService.findByClazz(clazz)
	}

}
