package com.akshay.ignite.sample.utility.functions.deploy

import com.akshay.ignite.sample.spark.SparkSummarizeService
import org.apache.ignite.{Ignite, IgniteServices, Ignition}

object DeployService extends App {
  private val CONFIG = "config/cache.xml"

  val ignite: Ignite = Ignition.start(CONFIG)

  val svcs: IgniteServices = ignite.services

  svcs.deployNodeSingleton("SummarizeServiceV2", new SparkSummarizeService())

}
