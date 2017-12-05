package com.hashmap.haf.workflow.task

import java.util.UUID
import org.apache.ignite.resources.ServiceResource
import scala.xml.NodeSeq

case class SparkTask(override val name: String,
                     override val id: UUID = UUID.randomUUID(),
                     jar: String,
                     mainClazz: String,
                     args: List[String],
                     override val to: Option[String] = None) extends EntityTask[String](name, id, to){

	@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
	protected val mapSvc = _

	override def execute(): String = {
		//mapSvc.runSurvice()
		???
	}
}

object SparkTask {

	def apply(xml: NodeSeq): SparkTask =
		new SparkTask(
			name = (xml \ "@name").text,
			jar = (xml \ "spark" \ "jar").text,
			mainClazz = (xml \ "spark" \ "mainClass").text,
			args = List[String]((xml \ "args").toList map {a => a.text}: _*)
		)

}

