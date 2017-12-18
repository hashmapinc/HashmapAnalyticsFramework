package com.hashmap.haf.functions.gateways

import java.io.File
import java.net.URI
import java.nio.file.Path

import com.hashmap.haf.functions.listeners.FunctionsChangeListener

trait FunctionsInputGateway {

	def listFilesFrom(uri: URI): List[Path]

	def readFileFrom(uri: URI): Option[File]

	def poll(uri: URI, listener: FunctionsChangeListener): Unit

}
