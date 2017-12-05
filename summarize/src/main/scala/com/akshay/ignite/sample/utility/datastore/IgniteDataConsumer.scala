package com.akshay.ignite.sample.utility.datastore

import com.akshay.ignite.sample.datastore.{DataframeIgniteCache, Datastore}
import org.apache.spark.sql.SparkSession



object IgniteDataConsumer extends App {


  val spark = SparkSession
    .builder()
    .appName("Ignite Data consumer")
    .master("local")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames

  val cache: Datastore = DataframeIgniteCache.create("config/cache.xml")
  val (schema, igniteRDD) = cache.get(spark.sparkContext, "input_data")
  schema.foreach(field => println(s"${field.name}: metadata=${field.metadata}"))
  spark.close()
}


