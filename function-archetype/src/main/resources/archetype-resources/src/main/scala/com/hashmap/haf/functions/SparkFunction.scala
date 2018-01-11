package com.hashmap.haf.functions

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.datastore.DataframeIgniteCache
import com.hashmap.haf.functions.constants.TaskConfigurationConstants._
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.ServiceContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}


class SparkFunction(configs: Map[String, String] = Map.empty[String, String]) extends ServiceFunction{

	var appName = ""
	@IgniteInstanceResource
	var ignite: Ignite = _

	override def run(inputKey: String, outputKey: String, functionArguments: Map[String, String], configurations: Map[String, String]): String = {
		val spark = SparkSession
			.builder()
			.appName(configurations.getOrElse(SPARK_APP_NAME, throw new IllegalArgumentException(SPARK_APP_NAME + " not provided in configurations")))
			.master(configurations.getOrElse(SPARK_MASTER, throw new IllegalArgumentException(SPARK_MASTER + " not provided in configurations")))
			.getOrCreate()

		import spark.implicits._
		val cache = DataframeIgniteCache.create()
		val (schema, igniteRDD) = cache.get(spark.sparkContext, inputKey)

		val rdd1: RDD[Row] = igniteRDD.map(_._2)
		val df = spark.sqlContext.createDataFrame(rdd1, schema)
		df.cache()
		val metadata = MetadataHandler.get(df)

		val newDs = MetadataHandler.set(df, metadata.build())

		cache.set(spark.sparkContext, newDs, outputKey)

		spark.close()

		"Result"

	}

	override def cancel(ctx: ServiceContext) = println("Cancelled")

	override def init(ctx: ServiceContext) = {
		appName = ctx.name()
	}

	override def execute(ctx: ServiceContext) = println("Executing")
}


object MetadataHandler {
	def get(df: DataFrame) = {
		val meta = new sql.types.MetadataBuilder()
		df.schema.filter(field => field.name == "app_metadata").headOption match {
			case Some(field) => meta.withMetadata(field.metadata)
			case _ => meta
		}
	}

	def set(df: DataFrame, newMetadata: Metadata) = {
		val newColumn = df.col(df.schema.fieldNames.apply(0)).as("app_metadata", newMetadata)
		df.withColumn("app_metadata", newColumn)
	}

}