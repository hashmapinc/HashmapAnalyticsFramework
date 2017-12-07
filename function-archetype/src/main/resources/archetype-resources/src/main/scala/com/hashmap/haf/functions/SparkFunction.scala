package com.hashmap.haf.functions

import com.hashmap.haf.datastore.DataframeIgniteCache
import com.hashmap.haf.functions.api.service.ServiceFunction
import org.apache.ignite.services.{Service, ServiceContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, DataFrame, Row, SparkSession}


class SparkFunction(configs: Map[String, String] = Map.empty[String, String]) extends ServiceFunction{

	var appName = ""
	val CONFIG = getClass.getResource("/cache.xml").getPath
	override def run(inputKey: String, outputKey: String, config: Any): Unit = {
		val spark = SparkSession
			.builder()
			.appName("Sample service")
			.master("local")
			.getOrCreate()

		import spark.implicits._
		val cache = DataframeIgniteCache.create(CONFIG)
		val (schema, igniteRDD) = cache.get(spark.sparkContext, inputKey)

		println("DF with following schema received: ")
		schema.foreach(field => println(s"${field.name}: metadata=${field.metadata}"))

		val rdd1: RDD[Row] = igniteRDD.map(_._2)
		val df = spark.sqlContext.createDataFrame(rdd1, schema)
		df.cache()
		val metadata = MetadataHandler.get(df)

		val newDs = MetadataHandler.set(df, metadata.build())

		cache.set(spark.sparkContext, newDs, outputKey)

		println("DF with following schema has been saved: ")
		newDs.schema.foreach(field => println(s"${field.name}: metadata=${field.metadata}"))

		spark.close()

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