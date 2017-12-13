package com.hashmap.haf.functions.api.factory

import java.lang.annotation.Annotation

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.api.processors.{AnnotationsProcessor, FunctionsAnnotationsProcessor, IgniteFunctionTypeReporter, Reporter}
import com.hashmap.haf.functions.api.transformer.{IgniteFunctionTransformer, Transformer}
import com.hashmap.haf.models.IgniteFunctionType
import eu.infomas.annotation.AnnotationDetector
import eu.infomas.annotation.AnnotationDetector.TypeReporter

object Factories {

	object Transformers{
		trait TransformerFactory[T <: Annotation, R]{
			def build(): Transformer[T, R]
		}

		object TransformerFactory{
			def apply[T <: Annotation, R](implicit ev: TransformerFactory[T, R]): Transformer[T, R] = ev.build()
		}

		implicit object IgniteTransformerFactory extends TransformerFactory[IgniteFunction, IgniteFunctionType]{
			override def build(): Transformer[IgniteFunction, IgniteFunctionType] = new IgniteFunctionTransformer
		}
	}

	object Reporters{
		import Transformers._

		trait TypeReporterFactory[T <: Annotation, R]{
			def build(): TypeReporter
		}

		object TypeReporterFactory{
			def apply[T <: Annotation, R]()(implicit ev: TypeReporterFactory[T, R]): TypeReporter = ev.build()
		}

		implicit object IgniteTypeReporterFactory extends TypeReporterFactory[IgniteFunction, IgniteFunctionType] {
			override def build(): TypeReporter =
				new IgniteFunctionTypeReporter(TransformerFactory[IgniteFunction, IgniteFunctionType])
		}
	}

	object Processors{
		import Reporters._
		 trait ProcessorFactory[T <: Annotation, R]{
			 def build(): AnnotationsProcessor[T, R]
		 }

		object ProcessorFactory{
			def apply[T <: Annotation, R](implicit ev: ProcessorFactory[T, R]): AnnotationsProcessor[T, R] = ev.build()
		}

		implicit object IgniteFunctionProcessorFactory extends ProcessorFactory[IgniteFunction, IgniteFunctionType]{
			override def build(): AnnotationsProcessor[IgniteFunction, IgniteFunctionType] = {
				val reporter = TypeReporterFactory[IgniteFunction, IgniteFunctionType]
				new FunctionsAnnotationsProcessor[IgniteFunction, IgniteFunctionType](new AnnotationDetector(reporter), reporter.asInstanceOf[Reporter[IgniteFunctionType]])
			}
		}
	}

}
