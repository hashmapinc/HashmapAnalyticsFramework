package com.hashmap.haf.functions.compiler

import org.springframework.stereotype.Service

import scala.reflect.internal.util.{AbstractFileClassLoader, BatchSourceFile}
import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.{Global, Settings}
import scala.util.{Failure, Success, Try}

@Service
class FunctionCompiler {

	private val target = new VirtualDirectory("(memory)", None)
	private val settings = compilerSettings
	private val global = new Global(settings)
	private lazy val run = new global.Run
	private val classLoader = new AbstractFileClassLoader(target, this.getClass.getClassLoader)

	def compile(clazzName: String, code: String): Unit = {
		val sourceFiles = List(new BatchSourceFile("(inline)", code))
		run.compileSources(sourceFiles)
	}

	def clazzBytes(clazzName: String): Option[Array[Byte]] ={
		Try(classLoader.classBytes(clazzName)) match {
			case Success(a) if a.isEmpty => None
			case Success(a) => Some(a)
			case Failure(_) => None
		}
	}

	def loadClazz(clazzName: String): Option[Class[_]] ={
		synchronized {
			Try(classLoader.loadClass(clazzName)) match{
				case Success(c) => Some(c)
				case Failure(_) => None
			}
		}
	}

	private def compilerSettings: Settings ={
		val settings = new Settings()
		settings.deprecation.value = true // enable detailed deprecation warnings
		settings.unchecked.value = true // enable detailed unchecked warnings
		settings.outputDirs.setSingleOutput(target)
		settings.usejavacp.value = true
		settings
	}

}

object FunctionCompiler {
	def apply(): FunctionCompiler = new FunctionCompiler()
}
