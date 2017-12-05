package com.akshay.ignite.sample.datastore

import org.apache.ignite.spark.IgniteRDD
import org.apache.spark.SparkContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}

trait Datastore {
  def set(sc: SparkContext, df: DataFrame, KEY: String)
  def get(sc: SparkContext, KEY: String):(StructType, IgniteRDD[String, Row])
}