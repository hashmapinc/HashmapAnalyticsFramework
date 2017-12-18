package com.hashmap.haf.functions.compiler

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.transformers.Transformer
import com.hashmap.haf.models.IgniteFunctionType
import org.specs2.mutable.Specification

class FunctionsCompilerSpec extends Specification{

	"Compiler" should {
		"compile simple source" in {
			val clazz =
				"""package com.hashmap
					|
					|class Hello{
					|println("hello world")
					|}
				""".stripMargin
			val compiler = FunctionCompiler()
			compiler.compile("com.hashmap.Hello", clazz)
			compiler.loadClazz("com.hashmap.Hello") should not be None
		}

		"compile dependent classes" in {
			val clazz = """package com.hashmap.haf.functions.transformers
				            |
				            |import java.lang.annotation.Annotation
				            |import com.hashmap.haf.annotations.IgniteFunction
				            |import com.hashmap.haf.models.{ConfigurationType, IgniteFunctionType}
				            |
				            |class IgniteFunctionTransformer extends Transformer[IgniteFunction, IgniteFunctionType]{
				            |	override def transform(annotation: IgniteFunction): IgniteFunctionType = {
				            |		val configs = annotation.configs().map { c =>
				            |			new ConfigurationType(c.key(), c.value())
				            |		}
				            |		new IgniteFunctionType(annotation.service(), configs)
				            |	}
				            |}""".stripMargin
			val compiler = FunctionCompiler()
			compiler.compile("com.hashmap.haf.functions.transformers.IgniteFunctionTransformer", clazz)
			compiler.loadClazz("com.hashmap.haf.functions.transformers.IgniteFunctionTransformer")
				.forall(classOf[Transformer[IgniteFunction, IgniteFunctionType]].isAssignableFrom) shouldEqual true
		}

		"return None if no compiled class found" in {
			val compiler = FunctionCompiler()
			compiler.loadClazz("com.hashmap.DoesNotExist") shouldEqual None
		}

		"return None in case compilation fails" in {
			val clazz = "class CompilationErrorCondition { println( }"
			val compiler = FunctionCompiler()
			compiler.compile("CompilationErrorCondition", clazz)
			compiler.loadClazz("CompilationErrorCondition") shouldEqual None
		}
	}

}
