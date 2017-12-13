package com.hashmap.haf.workflow.task

import java.util.UUID
import org.apache.ignite.resources.ServiceResource
import scala.xml.NodeSeq

case class LivyTask(override val name: String,
                    override val id: UUID = UUID.randomUUID(),
                    jar: String,
                    mainClazz: String,
                    args: List[String],
                    override val to: List[String] = List()) extends EntityTask[String](name, id, to){

	//@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
	//protected val mapSvc = _

	override def execute(): String = {
		//mapSvc.runSurvice()
		???
	}
}

object LivyTask {
	import com.hashmap.haf.workflow.constants.XmlConstants._

	def apply(xml: NodeSeq): LivyTask =
		new LivyTask(
			name = (xml \ NAME_ATTRIBUTE).text,
			jar = (xml \ LIVY_TASK \ JAR).text,
			mainClazz = (xml \ LIVY_TASK \ MAIN_CLAZZ).text,
			args = List[String]((xml \ LIVY_TASK \ ARGS \ ARG).toList map {a => a.text}: _*)
		)

}

