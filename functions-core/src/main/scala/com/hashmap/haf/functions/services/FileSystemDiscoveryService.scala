package com.hashmap.haf.functions.services

import java.net.URI
import java.nio.file.Path
import com.hashmap.haf.annotations.IgniteFunction
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.factory.Factories.Processors.ProcessorFactory
import com.hashmap.haf.functions.gateways.{FunctionsInputGateway, FunctionsOutputGateway}
import com.hashmap.haf.functions.processors.SourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FileSystemDiscoveryService @Autowired()(inputGateway: FunctionsInputGateway,
                                              outputGateway: FunctionsOutputGateway,
                                              sourceGenerator: SourceGenerator[IgniteFunctionType])
	extends FunctionsDiscoveryService{

	override def discoverFunctions(uri: URI): Unit = {
		val files = inputGateway.listFilesFrom(uri)
		process(uri, files)
	}

	def process(uri: URI, files: List[Path]): Unit ={
		val processor = ProcessorFactory[IgniteFunction, IgniteFunctionType]
		val compiler = FunctionCompiler()
		files.filter(isJar).foreach{ p =>
			inputGateway.readFileFrom(p.toUri).foreach{ f =>
				processor.process(f).foreach{ case (clazzName, function) =>
					val source = sourceGenerator.generateSource(function, clazzName)
					compiler.compile(clazzName, source)
					compiler.clazzBytes(clazzName).foreach(c => outputGateway.writeTo(new URI(s"${uri.toString}/$clazzName.class"), c))
				}
			}
		}
	}

	private def isJar(f: Path) = {
		f.endsWith(".jar")
	}
}
