package com.hashmap.haf.functions.services

import java.io.File
import java.net.URI
import java.nio.file.Path

import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.{FunctionsInputGateway, FunctionsOutputGateway}
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import com.hashmap.haf.functions.processors.{AnnotationsProcessor, SourceGenerator}
import com.hashmap.haf.models.IgniteFunctionType
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FileSystemDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                              outputGateway: FunctionsOutputGateway,
                                              sourceGenerator: SourceGenerator[IgniteFunctionType])
	extends FunctionsDiscoveryService{

	private val compiler = FunctionCompiler()

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
					compiler.compile(clazzName, s)
					compiler.clazzBytes(clazzName).foreach(c => outputGateway.writeTo(new URI(s"${f.getParentFile.toURI}/$clazzName.class"), c))
				case Left(m) => println(s"Error while generating source: ${m._1} exception is ${m._2.getLocalizedMessage} ")
			}
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
