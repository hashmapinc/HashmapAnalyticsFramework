package com.hashmap.haf.functions

import com.fasterxml.jackson.databind.ObjectMapper
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.DataframeIgniteCache
import com.hashmap.haf.functions.services.ServiceFunction
import com.hashmap.haf.models.{Column, DataSet}
import com.hashmap.haf.utils.SparkFunctionContext
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}

@IgniteFunction(functionClazz = "MetadataEditSparkTask",
	service = "metadataEditService",
	configs = Array())
class MetadataEditService extends ServiceFunction with SparkFunctionContext{

	//@IgniteInstanceResource
	//private var ignite: Ignite = _

	//val igniteConfig: String = getClass.getResource("/examples/cache.xml").toURI.toURL.toString

	override def run(inputKey: String, outputKey: String, config: Any): String = {
		//println("Ignite ", ignite.services().serviceDescriptors())

		/*val spark = SparkSession
			.builder()
			.appName("Spark Metadata Edit Service")
			.master("local")
			.getOrCreate()

		val args = config.asInstanceOf[Map[String, String]]
		val cache = DataframeIgniteCache.create(igniteConfig)
		val (schema, igniteRDD) = cache.get(spark.sparkContext, inputKey)

		val rdd1: RDD[Row] = igniteRDD.map(_._2)
		val df = spark.sqlContext.createDataFrame(rdd1, schema)
		df.cache()

		args.get("metadata").foreach{ m =>
			val mapper = new ObjectMapper()
			val dataSet = mapper.readValue(m, classOf[DataSet])
			val frame = df.selectExpr(buildExpression(dataSet.columns): _*)
			cache.set(spark.sparkContext, frame, outputKey)
		}*/
		"successful"
	}

	override def cancel(ctx: ServiceContext): Unit = {
		println("Cancelling execution of Metadata Edit function")
	}

	override def init(ctx: ServiceContext): Unit = {
		println("Metadata service function executing with ", ctx.executionId())
	}

	override def execute(ctx: ServiceContext): Unit = {
		println("Executing Metadata Edit service")
	}

	def buildExpression(columns: List[Column]): List[String] ={
		columns.map{ column =>
			column.metadata.conversion.map{ c =>
				(c.name, c.dataType) match {
					case (Some(n), Some(t)) if !t.equals(column.dataType) && !n.equals(column.name) =>
						s"${conversionSQL(column.dataType, t, column.name)} as $n"
					case (_, Some(t)) if !t.equals(column.dataType) =>
						s"${conversionSQL(column.dataType, t, column.name)} ${column.name}"
					case (Some(n), _) if !n.equals(column.name) => s"${column.name} as $n"
					case (_, _) => s"${column.name}"
				}
			}.getOrElse(s"${column.name}")
		}
	}

	def conversionSQL(from: String, to: String, columnName: String): String ={
		registry.findFunctionFor(from.toLowerCase, to.toLowerCase) match {
			case Some(f) => s"$f($columnName)"
			case None => s"cast($columnName as $to)"
		}
	}
}
