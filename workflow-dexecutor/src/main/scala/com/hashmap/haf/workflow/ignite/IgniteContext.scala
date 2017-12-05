package com.hashmap.haf.workflow.ignite

import java.util
import org.apache.ignite.{Ignite, Ignition}
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

object IgniteContext {
	val configuration = new IgniteConfiguration()
	val spi = new TcpDiscoverySpi()
	val finder = new TcpDiscoveryVmIpFinder()
	finder.setAddresses(util.Arrays.asList("192.168.1.88:47500..47510"))
	spi.setIpFinder(finder)
	configuration.setDiscoverySpi(spi)
	configuration.setClientMode(true)

	val ignite: Ignite = Ignition.start(configuration)
}
