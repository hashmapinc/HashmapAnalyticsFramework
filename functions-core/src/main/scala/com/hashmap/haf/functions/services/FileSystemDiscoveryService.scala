package com.hashmap.haf.functions.services

import java.io.File
import java.net.URI
import java.nio.file.Path
import javax.annotation.PostConstruct
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.deployment.{DefaultDeploymentService, DeploymentService}
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.{FunctionsInputGateway, FunctionsOutputGateway}
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.{AnnotationsProcessor, SourceGenerator}
import com.hashmap.haf.models.IgniteFunctionType
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

import scala.util.{Failure, Success, Try}

@Component
class FileSystemDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                              outputGateway: FunctionsOutputGateway,
                                              sourceGenerator: SourceGenerator[IgniteFunctionType])
	extends FunctionsDiscoveryService{

	private val compiler = FunctionCompiler()
	private val PACKAGE_NAME = "com.hashmap.haf.functions.extension"

	@Value("${functions.service.config}")
	var serviceConfig: String = _
	private var deploymentService: DeploymentService = _

	@PostConstruct
	def init(): Unit ={
		Option(serviceConfig) match {
			case Some(c) => deploymentService = DefaultDeploymentService(serviceConfig)
			case _ => deploymentService = DefaultDeploymentService()
		}
	}

	override def discoverFunctions(uri: URI): Unit = {
		val files = inputGateway.listFilesFrom(uri)
		process(uri, files)
		inputGateway.poll(uri, newListener())
	}

	def process(uri: URI, files: List[Path]): Unit ={
		val processor = ProcessorFactory[IgniteFunction, IgniteFunctionType]
		files.filter(isJar).foreach{ p =>
			inputGateway.readFileFrom(p.toUri).foreach{ f =>
				processAnnotations(processor, f)
			}
		}
	}

	private def processAnnotations(processor: AnnotationsProcessor[IgniteFunction, IgniteFunctionType], f: File) = {
		processor.process(f).foreach { case (clazzName, function) =>
			val source = sourceGenerator.generateSource(function)
			source match {
				case Right(s) =>
					val canonicalClazzName = s"$PACKAGE_NAME.${function.getFunctionClazz}"
					val directory = canonicalClazzName.replace('.', File.pathSeparatorChar)
					compiler.compile(canonicalClazzName, s)
					compiler.clazzBytes(canonicalClazzName).foreach(c => outputGateway.writeTo(new URI(s"${f.getParentFile.toURI}/$directory.class"), c))
					deployService(clazzName, function.getService)
				case Left(m) => println(s"Error while generating source: ${m._1} exception is ${m._2.getLocalizedMessage} ")
			}
		}
	}

	private def deployService(clazz: String, serviceName: String): Unit ={
		Try(Class.forName(clazz)) match {
			case Success(c) =>
				val instance = c.newInstance().asInstanceOf[ServiceFunction]
				deploymentService.deployNodeSingleton(serviceName, instance)

			case Failure(e) => throw new IllegalStateException("Error while deploying service", e)
		}
	}

	private def isJar(f: Path) = {
		f.endsWith(".jar")
	}

	private def newListener(): FunctionsChangeListener ={
		new FileSystemListener
	}

	class FileSystemListener extends FileAlterationListenerAdaptor with FunctionsChangeListener{
		override def onFileCreate(file: File): Unit = {
			println(" File created ", file.getAbsolutePath)
			val processor = ProcessorFactory[IgniteFunction, IgniteFunctionType]
			if(isJar(file.toPath)){
				processAnnotations(processor, file)
			}
		}

		override def onFileChange(file: File): Unit = {
			println(" File changed ", file.getAbsolutePath)
			val processor = ProcessorFactory[IgniteFunction, IgniteFunctionType]
			if(isJar(file.toPath)){
				processAnnotations(processor, file)
			}
		}

		override def onFileDelete(file: File): Unit = {
			println(" File deleted ")
		}
	}
}
