package com.hashmap.haf.functions.services

import java.io.{File, FileNotFoundException}
import java.net.URI
import javax.annotation.PostConstruct

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.deployment.DefaultDeploymentService
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.{FunctionsInputGateway, FunctionsOutputGateway}
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.{AnnotationsProcessor, SourceGenerator}
import com.hashmap.haf.models.{ConfigurationType, IgniteFunctionType}
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.ignite.services.ServiceConfiguration
import org.springframework.beans.factory.annotation.{Autowired, Value}

class FileSystemDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                              outputGateway: FunctionsOutputGateway,
                                              sourceGenerator: SourceGenerator[IgniteFunctionType])
	extends AbstractFunctionsDiscoveryService(inputGateway){

	type T = IgniteFunction
	type R = IgniteFunctionType

	private val compiler = FunctionCompiler()
	private val PACKAGE_NAME = "com.hashmap.haf.functions.extension"

	@Value("${functions.ignite.config}")
	var igniteConfig: String = _

	@PostConstruct
	def init(): Unit ={
		Option(igniteConfig) match {
			case Some(c) => deploymentService = DefaultDeploymentService(c)
			case _ => deploymentService = DefaultDeploymentService()
		}
	}

	override protected def validateFunctionInputLocation(uri: URI): Unit = {
		if(!new File(uri).exists()) throw new FileNotFoundException("Exception: File not found for uri: "+ uri)
	}

	override protected def processFunction(function: IgniteFunctionType): Unit = {
		val source = sourceGenerator.generateSource(function)
		source match {
			case Right(s) =>
				val canonicalClazzName = s"$PACKAGE_NAME.${function.getFunctionClazz}"
				val directory = canonicalClazzName.replace('.', File.pathSeparatorChar)
				compiler.compile(canonicalClazzName, s)
			//compiler.clazzBytes(canonicalClazzName).foreach(c => outputGateway.writeTo(new URI(s"${f.getParentFile.toURI}/$directory.class"), c))
			case Left(m) => println(s"Error while generating source: ${m._1} exception is ${m._2.getLocalizedMessage} ")
		}
	}

	override protected def newListener(): FunctionsChangeListener ={
		new FileSystemListener
	}

	override protected def newProcessor: AnnotationsProcessor[IgniteFunction, IgniteFunctionType] ={
		ProcessorFactory[IgniteFunction, IgniteFunctionType]
	}

	override protected def serviceNameFunction(r: IgniteFunctionType): String = r.getService

	class FileSystemListener extends FileAlterationListenerAdaptor with FunctionsChangeListener{
		override def onFileCreate(file: File): Unit = {
			println(" File created ", file.getAbsolutePath)
			processAnnotations(newProcessor, file)
		}

		override def onFileChange(file: File): Unit = {
			println(" File changed ", file.getAbsolutePath)
			processAnnotations(newProcessor, file)
		}

		override def onFileDelete(file: File): Unit = {
			println(" File deleted ")
		}
	}

	override protected def addConfigurations(r: IgniteFunctionType, cfg: ServiceConfiguration): ServiceConfiguration = {
		val configs: Array[ConfigurationType] = r.getConfigs
		val applyConfig = (k: String, f: (ConfigurationType) => Unit) => configs.find(_.getKey.equalsIgnoreCase(k)).foreach(f)
		if(configs != null){
			applyConfig(ConfigurationKeys.TOTAL_COUNT, c => cfg.setTotalCount(c.getIntValue))
			applyConfig(ConfigurationKeys.MAX_PER_NODE_COUNT, c => cfg.setMaxPerNodeCount(c.getIntValue))
			applyConfig(ConfigurationKeys.CACHE_NAME, c => cfg.setCacheName(c.getStringValue))
		}
		if(cfg.getMaxPerNodeCount <= 0 && cfg.getTotalCount <= 0){
			cfg.setMaxPerNodeCount(1) //Run service as node singleton
		}
		cfg
	}
}
