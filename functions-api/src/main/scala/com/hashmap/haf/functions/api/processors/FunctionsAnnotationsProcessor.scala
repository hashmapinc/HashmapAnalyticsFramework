package com.hashmap.haf.functions.api.processors

import java.io.File
import java.lang.annotation.Annotation
import com.hashmap.haf.annotations.IgniteFunction
import eu.infomas.annotation.AnnotationDetector
import eu.infomas.annotation.AnnotationDetector.TypeReporter
import scala.collection.mutable

class FunctionsAnnotationsProcessor(detector: AnnotationDetector,
                                    reporter: Reporter) {

	def detectAnnotations(jars: List[File]): Unit ={
		detector.detect(jars: _*)
		val annotations = reporter.detectedAnnotations
	}

}

object FunctionsAnnotationsProcessor {
	def apply(): FunctionsAnnotationsProcessor = {
		val reporter: TypeReporter = TypeReporter("ignite")
		new FunctionsAnnotationsProcessor(new AnnotationDetector(reporter), reporter.asInstanceOf[Reporter])
	}
}

trait Reporter extends TypeReporter{
	def detectedAnnotations: Map[String, Class[_ <: Annotation]]
}

class IgniteFunctionTypeReporter extends Reporter{
	val detected: mutable.HashMap[String, Class[_ <: Annotation]] = new mutable.HashMap()

	override def reportTypeAnnotation(annotation: Class[_ <: Annotation], className: String): Unit = {
		detected += (className -> annotation)
	}

	override def annotations(): Array[Class[_ <: Annotation]] = Array(classOf[IgniteFunction])

	override def detectedAnnotations: Map[String, Class[_ <: Annotation]] = detected.toMap
}

object TypeReporter{
	def apply(typ: String): TypeReporter = typ match {
		case "ignite" => new IgniteFunctionTypeReporter()
		case _ => throw new IllegalArgumentException("Undefined type of reporter")
	}
}