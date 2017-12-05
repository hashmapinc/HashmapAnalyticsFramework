package com.akshay.ignite.sample.utility.functions


import com.akshay.ignite.sample.spark.SparkService
import org.apache.ignite.{Ignite, IgniteServices, Ignition}


object SparkSummarizeClient extends App {

  private val CONFIG = "config/cache.xml"

  Ignition.setClientMode(true)

  val ignite: Ignite = Ignition.start(CONFIG)

  val svcs: IgniteServices = ignite.services

  val svc = ignite.services.serviceProxy("SummarizeServiceV2", classOf[SparkService], false)
  println(svc)
  println("Starting Execution")
  svc.runService("input_data", "output_data", config = null)
  println("Execution done")

  ignite.close()


}

