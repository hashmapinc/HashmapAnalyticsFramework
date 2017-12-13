package com.hashmap.haf.functions.api.processors

import java.io.File
import java.lang.annotation.Annotation
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.api.transformer.Transformer
import com.hashmap.haf.models.IgniteFunctionType
import eu.infomas.annotation.AnnotationDetector
import eu.infomas.annotation.AnnotationDetector.TypeReporter
import scala.collection.mutable

trait AnnotationsProcessor[T <: Annotation, R]{
	def detectAnnotations(jars: List[File]): Map[String, R]
}

class FunctionsAnnotationsProcessor[T <: Annotation, R](detector: AnnotationDetector,
                                                        reporter: Reporter[R]) extends AnnotationsProcessor[T, R]{

	override def detectAnnotations(jars: List[File]): Map[String, R] = {
		detector.detect(jars: _*)
		reporter.detectedAnnotations
	}
}

object AnnotationsProcessor {
	import com.hashmap.haf.functions.api.factory.Factories.Processors._
	def apply[T <: Annotation, R](implicit ev: ProcessorFactory[T, R]): AnnotationsProcessor[T, R] = ev.build()
}

trait Reporter[R] extends TypeReporter{
	def detectedAnnotations: Map[String, R]
}

class IgniteFunctionTypeReporter(transformer: Transformer[IgniteFunction, IgniteFunctionType]) extends Reporter[IgniteFunctionType]{
	val detected: mutable.HashMap[String, IgniteFunctionType] = new mutable.HashMap()

	override def reportTypeAnnotation(annotation: Class[_ <: Annotation], className: String): Unit = {
		val clazz = getClass.getClassLoader.loadClass(className)
		val function = clazz.getAnnotation(annotation).asInstanceOf[IgniteFunction]
		detected += (className -> transformer.transform(function))
	}

	override def annotations(): Array[Class[_ <: Annotation]] = Array(classOf[IgniteFunction])

	override def detectedAnnotations: Map[String, IgniteFunctionType] = detected.toMap
}