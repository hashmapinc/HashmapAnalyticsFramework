package com.hashmap.haf.utils

import java.util

import com.hashmap.haf.functions.FunctionsRegistry
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spark.IgniteContext
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Duration, StreamingContext}

trait SparkFunctionContext {
	val sparkConf: SparkConf = new SparkConf().setAppName("EditDatasetMetadata").setMaster("local[1]")
	val ssc: StreamingContext = new StreamingContext(sparkConf, Duration(10000L))
	val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
	val registry = new FunctionsRegistry()
	registry.registerBuiltInFunctions(spark.sqlContext)
	ssc.sparkContext.setLogLevel("WARN")

	val igniteContext = new IgniteContext(spark.sparkContext, () => {
		val configuration = new IgniteConfiguration()
		val spi = new TcpDiscoverySpi()
		val finder = new TcpDiscoveryVmIpFinder()
		finder.setAddresses(util.Arrays.asList("192.168.1.88:47500..47510"))
		spi.setIpFinder(finder)
		configuration.setDiscoverySpi(spi)
		configuration
	})
}
