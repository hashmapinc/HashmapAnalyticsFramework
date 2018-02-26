package com.hashmap.haf.datastore.examples

import com.hashmap.haf.datastore.impl.{IgniteSparkDFStore, SparkDFOptions}
import com.typesafe.config.ConfigFactory
import org.apache.ignite.spark.IgniteDataFrameSettings._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{Encoder, SparkSession}


case class DeviceIoTData (battery_level: Long, c02_level: Long, cca2: String,
                          cca3: String, cn: String, device_id: Long, device_name: String,
                          humidity: Long, ip: String, latitude: Double, lcd: String, longitude: Double,
                          scale:String, temp: Long, timestamp: Long)


object IgniteDataProducer extends App {

  println("Example to write DF in ignite cache")

  val tableName = "iot_devices"
  private [this] val configFactory = ConfigFactory.load()
  private[this] val CONFIG:String = configFactory.getString("ignite.configPath")

  import org.apache.ignite.Ignition

  Ignition.setClientMode(true)
  val ignite = Ignition.start(CONFIG)

  val spark = SparkSession
    .builder()
    .appName("Ignite Data Producer")
    .master("local")
    .getOrCreate()


  Logger.getRootLogger.setLevel(Level.INFO)
  Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

  val df = InputDataLoader.load[DeviceIoTData](spark, getClass.getResource("/examples/iot_devices.json").getPath).toDF()

  println()
  println("Writing Data Frame to Ignite:")
  println()

  //Writing content of data frame to Ignite.

  IgniteSparkDFStore.set(df, SparkDFOptions(spark, tableName))

  println("Done!")

  spark.close()
  ignite.close()
}

object InputDataLoader {
  private[datastore] def load[T <: Product : Encoder](spark: SparkSession, pathToData: String) = {
    spark.read.json(pathToData).as[T]
  }
}

