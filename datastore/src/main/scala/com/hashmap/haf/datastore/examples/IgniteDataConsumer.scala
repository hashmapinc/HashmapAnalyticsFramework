package com.hashmap.haf.datastore.examples

import com.hashmap.haf.datastore.{DataframeIgniteCache, Datastore}
import org.apache.spark.sql.SparkSession


object IgniteDataConsumer extends App {


  val spark = SparkSession
    .builder()
    .appName("Ignite Data consumer")
    .master("local")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames

  val cache: Datastore = DataframeIgniteCache.create()
  val (schema, igniteRDD) = cache.get(spark.sparkContext, "output_data")
  schema.foreach(field => println(s"${field.name}: metadata=${field.metadata}"))
  spark.close()
}


