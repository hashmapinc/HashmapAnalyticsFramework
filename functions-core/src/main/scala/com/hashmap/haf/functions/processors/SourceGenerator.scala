package com.hashmap.haf.functions.processors

import java.io.StringWriter
import java.util.Properties
import com.hashmap.haf.models.IgniteFunctionType
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

trait SourceGenerator[T] {
	def generateSource(function: T, clazz: String): String
}

@Service
class VelocitySourceGenerator extends SourceGenerator[IgniteFunctionType] {

	@Value("${functions.source.template}")
	var template: String = _
	lazy val ve: VelocityEngine = initializeVelocityEngine("velocity/velocity.properties")

	@Value("${functions.output.location}")
	var output: String = _

	override def generateSource(function: IgniteFunctionType, clazz: String): String = {
		val writer = new StringWriter
		try {
			val vc = new VelocityContext
			vc.put("model", function)
			//val tp = "templates/task.vm"
			val vt = ve.getTemplate(template)
			vt.merge(vc, writer)
			writer.toString
		}finally {
			writer.close()
		}
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
