package com.hashmap.haf.functions

import java.util

import com.hashmap.haf.functions.services.ServiceFunction
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.{Ignite, Ignition}

import scala.collection.JavaConverters._

/**
  * Created by jetinder on 02/01/18.
  */
object TestIgniteServices extends App{

  //todo for testing purposes will be removed later
  val configuration = new IgniteConfiguration()
  val spi = new TcpDiscoverySpi()
  val finder = new TcpDiscoveryVmIpFinder()
  finder.setAddresses(util.Arrays.asList("192.168.1.88:47500..47510"))
  spi.setIpFinder(finder)
  configuration.setDiscoverySpi(spi)
  //configuration.setClientMode(true)

  val ignite: Ignite = Ignition.start(configuration)

  val cfg = new ServiceConfiguration()
  cfg.setName("metadataEditService")
  cfg.setTotalCount(1)
  cfg.setService(new MetadataEditService())

  ignite.services().deploy(cfg)

  Thread.sleep(10000)

  private val descriptors = ignite.services().serviceDescriptors().asScala.toList

  descriptors.foreach {
    c =>
      println(classOf[ServiceFunction].isAssignableFrom(c.serviceClass()))
      println(c.totalCount(), c.topologySnapshot())
      println(c)
      val service = ignite.services().serviceProxy("metadataEditService", classOf[ServiceFunction], false)
      println(service.run("", "", null))
  }
}
