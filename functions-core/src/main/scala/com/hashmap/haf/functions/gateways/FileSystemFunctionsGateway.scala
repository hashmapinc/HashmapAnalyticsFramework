package com.hashmap.haf.functions.gateways

import java.io.{File, FileOutputStream}
import java.net.URI
import java.nio.file._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.hashmap.haf.functions.listeners.FunctionsChangeListener
import org.apache.commons.io.monitor.{FileAlterationListener, FileAlterationMonitor, FileAlterationObserver}
import org.springframework.stereotype.Component

import scala.util.{Failure, Success, Try}

@Component
class FileSystemFunctionsGateway extends FunctionsInputGateway with FunctionsOutputGateway{
	implicit val system: ActorSystem = ActorSystem()
	implicit val materializer = ActorMaterializer()

	override def listFilesFrom(uri: URI): List[Path] = {
		Paths.get(uri).toFile.listFiles().toList.map(_.toPath)
	}

	override def poll(uri: URI, listener: FunctionsChangeListener): Unit = {
		val fs = FileSystems.getDefault
		val monitor: FileAlterationMonitor = new FileAlterationMonitor(1 * 1000)
		val observer = new FileAlterationObserver(fs.getPath(uri.getPath).toFile)
		observer.addListener(listener.asInstanceOf[FileAlterationListener])
		monitor.addObserver(observer)
		monitor.start()
	}

	override def writeTo(uri: URI, data: Array[Byte]): Unit = {
		val stream = new FileOutputStream(uri.getPath)
		try{
			stream.write(data)
		}finally{
			stream.close()
		}
	}

	override def readFileFrom(uri: URI): Option[File] = {
		Try(Paths.get(uri).toFile) match {
			case Success(f) => Some(f)
			case Failure(_) => None
		}
	}
}
