package com.hashmap.haf.functions.summarize.example

import com.hashmap.haf.functions.summarize.SparkSummarizeService
import org.apache.ignite.{Ignite, IgniteServices, Ignition}

object DeployService extends App {
  private val CONFIG = getClass.getResource("/examples/cache.xml").getPath

  val ignite: Ignite = Ignition.start(CONFIG)

  val svcs: IgniteServices = ignite.services

  svcs.deployNodeSingleton("SummarizeServiceV2", new SparkSummarizeService())

}
