package com.hashmap.haf.utils

import com.hashmap.haf.functions.FunctionsRegistry
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkFunctionContext {
	val sparkConf: SparkConf = new SparkConf().setAppName("EditDatasetMetadata").setMaster("local[1]")
	val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
	val registry = new FunctionsRegistry()
	registry.registerBuiltInFunctions(spark.sqlContext)
}
