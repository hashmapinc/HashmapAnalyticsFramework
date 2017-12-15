package com.hashmap.haf.functions.api.processors

import java.util.Properties

import com.hashmap.haf.models.IgniteFunctionType
import org.apache.velocity.{Template, VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

trait SourceGenerator[T] {
	def generateSource(function: T, clazz: String): Unit
}

@Service
class VelocitySourceGenerator extends SourceGenerator[IgniteFunctionType] {
	lazy val ve: VelocityEngine = initializeVelocityEngine("velocity/velocity.properties")

	@Value("${functions.output.location}")
	var output: String = _

	override def generateSource(function: IgniteFunctionType, clazz: String): Unit = {
		val vc = new VelocityContext
		vc.put("model", function)
		val ct = ve.getTemplate("templates/task.vm")
	}

	private def initializeVelocityEngine(resource: String): VelocityEngine ={
		val props = new Properties
		val url = this.getClass.getClassLoader.getResource(resource)
		props.load(url.openStream)
		val ve = new VelocityEngine(props)
		ve.init()
		ve
	}
}
