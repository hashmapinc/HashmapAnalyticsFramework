package com.hashmap.haf.datastore

import org.apache.ignite.spark.IgniteRDD
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.StructType

trait Datastore {
  def set(sc: SparkContext, df: DataFrame, KEY: String)
  def get(sc: SparkContext, KEY: String):(StructType, IgniteRDD[String, Row])
}