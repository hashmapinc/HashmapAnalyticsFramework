package com.hashmap.haf.functions.jdbcReader

import java.util

import com.hashmap.haf.functions.constants.TaskConfigurationConstants._
import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.{Ignite, Ignition}

/**
  * Created by jetinder on 02/01/18.
  */
object TestIgniteServices extends App{

  //todo for testing purposes will be removed later
  val configuration = new IgniteConfiguration()
  configuration.setPeerClassLoadingEnabled(true)
  val spi = new TcpDiscoverySpi()
  val finder = new TcpDiscoveryVmIpFinder()
  finder.setAddresses(util.Arrays.asList("192.168.1.98:47500..47510"))
  spi.setIpFinder(finder)
  configuration.setDiscoverySpi(spi)
  configuration.setClientMode(true)


  val ignite: Ignite = Ignition.start(configuration)

  val cfg = new ServiceConfiguration()
  cfg.setName("JdbcReaderService")
  cfg.setTotalCount(1)
  cfg.setService(new JdbcReaderService())

  ignite.services().deployClusterSingleton("JdbcReaderService", new JdbcReaderService())
  //ignite.services(ignite.cluster().forRemotes().forServers()).deploy(cfg)

  Thread.sleep(10000)

  try {
    val service = ignite.services().serviceProxy("JdbcReaderService", classOf[ServiceFunction], false)


    service.run("", "output_postgres", Map(JDBC_URL -> "jdbc:postgresql://172.17.0.2:5432/thingsboard",
      JDBC_DB_TABLE -> "ts_kv",
      JDBC_USER -> "postgres",
      JDBC_PASSWORD -> "postgres"), Map(SPARK_APP_NAME -> "Sample", SPARK_MASTER -> "local"))

    println()
    println("Hello")

  } finally {
    ignite.close()
  }

  /*private val descriptors = ignite.services().serviceDescriptors().asScala.toList

  descriptors.foreach {
    c =>
      println(classOf[ServiceFunction].isAssignableFrom(c.serviceClass()))
      println(c.totalCount(), c.topologySnapshot())
      println(c)
      val service = ignite.services().serviceProxy("JdbcReaderService", classOf[ServiceFunction], false)
      println(service.run("", "output_postgres", Map(JDBC_URL -> "jdbc:postgresql://172.17.0.2:5432/thingsboard",
        JDBC_DB_TABLE -> "ts_kv",
        JDBC_USER -> "postgres",
        JDBC_PASSWORD -> "postgres"), Map(SPARK_APP_NAME -> "Sample", SPARK_MASTER -> "local")))
      println("Hello")
  }*/
  //ignite.services().cancelAll()
  //ignite.close()


}
/*

object Testing extends App {
  val configuration = new IgniteConfiguration()
  configuration.setPeerClassLoadingEnabled(true)
  val spi = new TcpDiscoverySpi()
  val finder = new TcpDiscoveryVmIpFinder()
  finder.setAddresses(util.Arrays.asList("192.168.1.98:47500..47510"))
  spi.setIpFinder(finder)
  configuration.setDiscoverySpi(spi)
  configuration.setClientMode(true)


  val ignite: Ignite = Ignition.start(configuration)

  val cfg = new ServiceConfiguration()
  cfg.setName("JdbcReaderService")
  cfg.setTotalCount(1)
  cfg.setService(new JdbcReaderService())

  ignite.services(ignite.cluster().forRemotes().forServers()).deploy(cfg)
  val compute = ignite.compute()

  compute.run(new IgniteRunnable {
    @ServiceResource(serviceName = "JdbcReaderService")
    var service:JdbcReaderService = _
    override def run(): Unit = {
      println("Running")
      println(service.run("", "output_postgres", Map(JDBC_URL -> "jdbc:postgresql://172.17.0.2:5432/thingsboard",
        JDBC_DB_TABLE -> "ts_kv",
        JDBC_USER -> "postgres",
        JDBC_PASSWORD -> "postgres"), Map(SPARK_APP_NAME -> "Sample", SPARK_MASTER -> "local")))
      println("Done")
    }
  })



}*/
