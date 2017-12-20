package com.hashmap.haf.functions.transformers

import com.hashmap.haf.annotations.{Configuration, IgniteFunction}
import com.hashmap.haf.functions.factory.Factories.Transformers.TransformerFactory
import com.hashmap.haf.functions.transformers.AnnotationCreator.TestIgniteFunction
import com.hashmap.haf.models.IgniteFunctionType
import org.specs2.mutable.Specification

class IgniteFunctionTransformerSpec extends Specification{

	"Ignite function transformer" should {
		"transform Ignite function annotation to type" in {
			val function: IgniteFunction = classOf[TestIgniteFunction].getAnnotation(classOf[IgniteFunction])
			val transformer = TransformerFactory[IgniteFunction, IgniteFunctionType]
			val result = transformer.transform(function)

			result.getFunctionClazz shouldEqual "TestTask"
			result.getService shouldEqual "testService"
			result.getConfigs.length shouldEqual 1
		}
	}

}

object AnnotationCreator{

	@IgniteFunction(functionClazz = "TestTask", service = "testService", configs = Array(
		new Configuration(key = "ignite.executors", value = "1")
	))
	class TestIgniteFunction
}
