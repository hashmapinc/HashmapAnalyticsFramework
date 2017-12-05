package com.akshay.ignite.sample.spark

trait SparkService {
  def runService(inputKey: String, outputKey:String, config: Any): Unit
}
