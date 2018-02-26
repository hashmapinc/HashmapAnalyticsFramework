package com.hashmap.haf.datastore.examples


import com.hashmap.haf.datastore.api.SELECT
import com.hashmap.haf.datastore.impl.IgniteQueryableDataStore
import com.typesafe.config.ConfigFactory
import org.apache.ignite.Ignition

import scala.collection.JavaConversions._

object IgniteCacheQueryConsumer extends App {
  println("Reading cache value directly from ignite")

  val CACHE_NAME = "testCache"

  private [this] val configFactory = ConfigFactory.load()
  private[this] val CONFIG:String = configFactory.getString("ignite.configPath")
  val tableName = "iot_devices"

  Ignition.setClientMode(true)
  val ignite = Ignition.start(CONFIG)

  val datastore = IgniteQueryableDataStore(ignite)

  val data = datastore.query(s"SELECT * FROM $tableName limit 20", SELECT)


  println("***********************************************************************")
  data.foreach { row ⇒ println(row.mkString("[", ", ", "]")) }
  println("***********************************************************************")

  ignite.close()
}
