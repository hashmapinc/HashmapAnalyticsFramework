package com.hashmap.haf.functions.api.transformer

import java.lang.annotation.Annotation
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.models.IgniteFunctionType

trait Transformer[T <: Annotation, R] {
	def transform(annotation : T): R
}

class IgniteFunctionTransformer extends Transformer[IgniteFunction, IgniteFunctionType]{
	override def transform(annotation: IgniteFunction): IgniteFunctionType = new IgniteFunctionType("service", null)
}
