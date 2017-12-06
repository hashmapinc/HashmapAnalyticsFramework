package com.hashmap.haf.functions.summarize.example

import com.hashmap.haf.functions.api.ServiceFunction
import org.apache.ignite.{Ignite, IgniteServices, Ignition}


object SparkSummarizeClient extends App {

  private val CONFIG = getClass.getResource("/examples/cache.xml").getPath

  Ignition.setClientMode(true)

  val ignite: Ignite = Ignition.start(CONFIG)

  val svcs: IgniteServices = ignite.services

  val svc = ignite.services.serviceProxy("SummarizeServiceV2", classOf[ServiceFunction], false)
  println(svc)
  println("Starting Execution")
  svc.run("input_data", "output_data", config = null)
  println("Execution done")

  ignite.close()


}

