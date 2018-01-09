package com.hashmap.haf.functions.transformers

import java.lang.annotation.Annotation

import com.hashmap.haf.annotations.{IgniteFunction, LivyFunction}
import com.hashmap.haf.models.{ConfigurationType, IgniteFunctionType, LivyFunctionType}

trait Transformer[T <: Annotation, R] {
	def transform(annotation : T): R
}

class IgniteFunctionTransformer extends Transformer[IgniteFunction, IgniteFunctionType]{
	override def transform(annotation: IgniteFunction): IgniteFunctionType = {
		val configs = annotation.configs().map { c =>
			new ConfigurationType(c.key(), c.value())
		}
		new IgniteFunctionType(annotation.service(), configs, annotation.functionClazz(), annotation.packageName())
	}
}

class LivyFunctionTransformer extends Transformer[LivyFunction, LivyFunctionType]{
	override def transform(annotation: LivyFunction): LivyFunctionType = {
		val configs = annotation.configs().map { c =>
			new ConfigurationType(c.key(), c.value())
		}
		new LivyFunctionType(annotation.mainClass(), annotation.jar(), annotation.functionClass(), configs)
	}
}