package com.hashmap.haf.workflow.task

import java.util.UUID

import com.hashmap.haf.workflow.util.UUIDConverter
import scala.xml.{Elem, NodeSeq}

case class LivyTask(override val name: String,
                    override val id: UUID = UUID.randomUUID(),
                    jar: String,
                    mainClazz: String,
                    args: List[String],
                    override val to: List[String] = List()) extends BaseTask[String](name, id, to){

	//@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
	//protected val mapSvc = _

	override def execute(): String = {
		//mapSvc.runSurvice()
		???
	}

	override def toXml: Elem = {
		//todo complete this when needed
		<Some></Some>
	}
}

object LivyTask {
	import com.hashmap.haf.workflow.constants.XmlConstants._

	def apply(xml: NodeSeq): LivyTask = {
		val idString = (xml \ ID_ATTRIBUTE).text
		new LivyTask(
			name = (xml \ NAME_ATTRIBUTE).text,
			id = if (idString != null && idString.nonEmpty) UUIDConverter.fromString(idString) else UUID.randomUUID(),
			jar = (xml \ LIVY_TASK \ JAR).text,
			mainClazz = (xml \ LIVY_TASK \ MAIN_CLAZZ).text,
			args = List[String]((xml \ LIVY_TASK \ ARGS \ ARG).toList map { a => a.text }: _*)
		)
	}
}

