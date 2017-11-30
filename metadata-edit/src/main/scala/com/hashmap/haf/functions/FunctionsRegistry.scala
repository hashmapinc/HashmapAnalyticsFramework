package com.hashmap.haf.functions

import org.apache.spark.sql.SQLContext
import scala.reflect.runtime.universe.TypeTag

class FunctionsRegistry {
	type FunctionTypes = (String, String)
	protected var registry: Map[(String, String), String] = Map.empty

	def register[RT: TypeTag, I: TypeTag](name: String, func: (I) => RT, alias: FunctionTypes)(implicit sqlContext: SQLContext): Unit = {
		//Register UDF to SQLContext
		sqlContext.udf.register(name, func)
		//Have a local copy of those functions for faster lookup

		registry += alias -> name
		println("Check compile")
		/*val f: Map[String, Predef.Function[_, _]] = registry.getOrElse((typeOf[I], typeOf[RT]), Map.empty)
		registry += (typeOf[I], typeOf[RT]) -> (f + name -> func)*/
	}

	def registerBuiltInFunctions(implicit sqlContext: SQLContext): Unit ={
		register("toUUID", Functions.stringToUUID, ("string", "uuid"))
	}

	def findFunctionFor(types: FunctionTypes): Option[String] = registry.get(types)
}

object Functions{
	def stringToUUID(uuid: String): String = {
		java.util.UUID.fromString(
			uuid
				.replaceFirst(
					"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
				)
		).toString
	}
}