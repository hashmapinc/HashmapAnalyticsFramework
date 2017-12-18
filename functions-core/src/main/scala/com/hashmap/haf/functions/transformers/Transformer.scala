package com.hashmap.haf.functions.transformers

import java.lang.annotation.Annotation
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.models.{ConfigurationType, IgniteFunctionType}

trait Transformer[T <: Annotation, R] {
	def transform(annotation : T): R
}

class IgniteFunctionTransformer extends Transformer[IgniteFunction, IgniteFunctionType]{
	override def transform(annotation: IgniteFunction): IgniteFunctionType = {
		val configs = annotation.configs().map { c =>
			new ConfigurationType(c.key(), c.value())
		}
		new IgniteFunctionType(annotation.service(), configs, annotation.functionClazz())
	}
}
