package com.hashmap.haf.functions.processors

import java.io.StringWriter
import java.util.Properties

import com.hashmap.haf.models.IgniteFunctionType
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import scala.util.{Failure, Success, Try}

trait SourceGenerator[T] {
	def generateSource(function: T): Either[(String, Throwable), String]
}

@Service
class VelocitySourceGenerator extends SourceGenerator[IgniteFunctionType] {

	@Value("${functions.source.template}")
	var template: String = _
	lazy val ve: VelocityEngine = initializeVelocityEngine("velocity/velocity.properties")

	override def generateSource(function: IgniteFunctionType): Either[(String, Throwable), String] = {
		val vc = new VelocityContext
		vc.put("model", function)
		Try(ve.getTemplate(template)) match{
			case Success(vt) =>
				val writer = new StringWriter
				vt.merge(vc, writer)
				Right(writer.toString)
			case Failure(e) => Left("Error while loading template", e)
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
