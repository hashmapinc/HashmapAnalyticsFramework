package com.hashmap.haf.datastore.examples

import com.hashmap.haf.datastore.{DataframeIgniteCache, Datastore}
import org.apache.spark.sql.{Encoder, SparkSession}


case class DeviceIoTData (battery_level: Long, c02_level: Long, cca2: String,
                          cca3: String, cn: String, device_id: Long, device_name: String,
                          humidity: Long, ip: String, latitude: Double, lcd: String, longitude: Double,
                          scale:String, temp: Long, timestamp: Long)


object IgniteDataProducer extends App {

  /*private val igConfig = getClass.getResource("/cache.xml").getPath
  private val ignite: Ignite = Ignition.start(igConfig)*/


  val spark = SparkSession
    .builder()
    .appName("Ignite Data Producer")
    .master("local")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

  val df = InputDataLoader.load[DeviceIoTData](spark, getClass.getResource("/examples/iot_devices.json").getPath).toDF()

  val cache: Datastore = DataframeIgniteCache.create()
  cache.set(spark.sparkContext, df, "input_data")
  spark.close()
}

object InputDataLoader {
  private[datastore] def load[T <: Product : Encoder](spark: SparkSession, pathToData: String) = {
    spark.read.json(pathToData).as[T]
  }
}

