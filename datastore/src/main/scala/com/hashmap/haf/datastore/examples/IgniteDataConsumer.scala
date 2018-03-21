package com.hashmap.haf.datastore.examples

import com.hashmap.haf.datastore.impl.{IgniteSparkDFStore, SparkDFOptions}
import com.typesafe.config.ConfigFactory
import org.apache.ignite.Ignition
import org.apache.spark.sql.SparkSession

object IgniteDataConsumer extends App {

  println("Reading DF from ignite cache")
  private [this] val configFactory = ConfigFactory.load()
  private[this] val CONFIG:String = configFactory.getString("ignite.configPath")
  val tableName = "iot_devices"

  Ignition.setClientMode(true)
  val ignite = Ignition.start(CONFIG)

  val spark = SparkSession
    .builder()
    .appName("Ignite Data Consumer")
    .master("local")
    .getOrCreate()



  val df1 = IgniteSparkDFStore.get(SparkDFOptions(spark, tableName))
  println("***********************************************************************")
  df1.show()
  println("***********************************************************************")


  ignite.close()
}


