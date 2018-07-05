package com.hashmap.haf.execution.mappers

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

class ScalaObjectMapperBuilder extends Jackson2ObjectMapperBuilderCustomizer{
	override def customize(builder: Jackson2ObjectMapperBuilder): Unit = {
		builder.modules(DefaultScalaModule)
	}
}
