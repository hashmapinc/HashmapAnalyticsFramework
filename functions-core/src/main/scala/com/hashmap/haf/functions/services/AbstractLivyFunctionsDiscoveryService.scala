package com.hashmap.haf.functions.services

import java.io.File
import java.lang.annotation.Annotation
import java.net.{URI, URL, URLClassLoader}
import java.nio.file.Path

import com.hashmap.haf.functions.gateways.FunctionsInputGateway
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.AnnotationsProcessor
import org.apache.ignite.services.ServiceConfiguration

abstract class AbstractLivyFunctionsDiscoveryService(inputGateway: FunctionsInputGateway) extends FunctionsDiscoveryService{

	type T <: Annotation
	type R

	override def discoverFunctions(uri: URI): Unit = {
		val files = inputGateway.listFilesFrom(uri)
		process(uri, files)
		inputGateway.poll(uri, newListener())
	}

	def process(uri: URI, files: List[Path]): Unit ={
		val jars = files.filter(isJar)
		addJarsToClassPath(jars.map(_.toUri.toURL).toArray)
		jars.foreach{ p =>
			inputGateway.readFileFrom(p.toUri).foreach{ f =>
				processAnnotations(newProcessor, f)
			}
		}
	}

	protected def addJarsToClassPath(urls: Array[URL]): Unit = synchronized {
		try {
			val contextClassLoader = getClass.getClassLoader

			contextClassLoader match {
				case c: URLClassLoader =>
					val method = classOf[URLClassLoader].getDeclaredMethod("addURL", classOf[URL])
					method.setAccessible(true)
					urls.foreach(u => method.invoke(c, u))
				case _ => println("URLS not added on classpath")
			}
		} catch {
			case e: Exception =>
				println("Error occurred")
		}
	}

	protected def processAnnotations(processor: AnnotationsProcessor[T, R],
	                                 f: File): Unit ={
		if(isJar(f.toPath)) {
			processor.detect(f).foreach { case (serviceName, r) =>
				deployJarOnLivyServer(f)
				processFunction(r)
			}
		}
	}

	protected def processFunction(r: R): Unit

	protected def deployJarOnLivyServer(f: File): Unit

	protected def isJar(f: Path): Boolean = {
		f.toString.endsWith(".jar")
	}

	protected def addConfigurations(r: R, cfg: ServiceConfiguration): ServiceConfiguration

	protected def newListener(): FunctionsChangeListener

	protected def newProcessor: AnnotationsProcessor[T, R]
}
