package com.hashmap.haf.functions.processors

import java.io.File
import java.lang.annotation.Annotation
import java.net.{URI, URL, URLClassLoader}

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.transformers.Transformer
import com.hashmap.haf.models.IgniteFunctionType
import eu.infomas.annotation.AnnotationDetector
import eu.infomas.annotation.AnnotationDetector.TypeReporter

import scala.collection.mutable

trait AnnotationsProcessor[T <: Annotation, R]{
	def detect(jars: File): Map[String, R]
}

class FunctionsAnnotationsProcessor[T <: Annotation, R](detector: AnnotationDetector,
                                                        reporter: Reporter[R]) extends AnnotationsProcessor[T, R]{

	override def detect(jars: File): Map[String, R] = {
		detector.detect(jars)
		val annotations = reporter.detectedAnnotations
		annotations
	}
}

trait Reporter[R] extends TypeReporter{
	def detectedAnnotations: Map[String, R]
}

class IgniteFunctionTypeReporter(transformer: Transformer[IgniteFunction, IgniteFunctionType]) extends Reporter[IgniteFunctionType]{
	val detected: mutable.HashMap[String, IgniteFunctionType] = new mutable.HashMap()

	override def reportTypeAnnotation(annotation: Class[_ <: Annotation], className: String): Unit = {
		//val classloader = new URLClassLoader(Array(new URL("file:///Users/jetinder/timeseries/HashmapAnalyticsFramework/functions/summarize/target/summarize-1.0.0-SNAPSHOT.jar")))
		val c = Thread.currentThread.getContextClassLoader.asInstanceOf[URLClassLoader]
		val clazz = c.loadClass(className)
		val cl = Class.forName(className)
		val function = cl.getAnnotation(annotation).asInstanceOf[IgniteFunction]
		detected += (className -> transformer.transform(function))
	}

	override def annotations(): Array[Class[_ <: Annotation]] = Array(classOf[IgniteFunction])

	override def detectedAnnotations: Map[String, IgniteFunctionType] = detected.toMap
}