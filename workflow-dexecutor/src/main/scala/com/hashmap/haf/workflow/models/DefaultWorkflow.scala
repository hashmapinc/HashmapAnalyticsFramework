package com.hashmap.haf.workflow.models

import java.util.UUID
import com.github.dexecutor.core.Dexecutor
import com.hashmap.haf.workflow.constants.XmlConstants
import com.hashmap.haf.workflow.task.EntityTask
import com.hashmap.haf.workflow.factory.Factory._
import scala.xml.Node

case class DefaultWorkflow(tasks: List[EntityTask[String]], name: String)
	extends Workflow[UUID, String](tasks, name){

	val id: UUID = UUID.randomUUID()

	override def getId: UUID = id

	def buildTaskGraph(executor: Dexecutor[UUID, String]): Unit = {
		tasks.foreach(t => {
			val toTask: Option[EntityTask[String]] =
				t.to.map{n =>
					if(n.equalsIgnoreCase("end")) None
					else tasks.find(_.name.equalsIgnoreCase(n))
				}.getOrElse(throw new IllegalStateException("No to task defined"))
			toTask match {
				case Some(et) => executor.addDependency(t.getId, et.getId)
				case _ =>  executor.addIndependent(t.id)
			}
		})
	}
}

object DefaultWorkflow{
	import XmlConstants._

	def apply(): DefaultWorkflow = new DefaultWorkflow(Nil, "EmptyWorkflow")

	def apply(xml: Node): DefaultWorkflow = {
		new DefaultWorkflow(
			name = (xml \ NAME_ATTRIBUTE).text,
			tasks = List[EntityTask[String]](
				(xml \ TASK).toList map {s => TaskFactory[UUID, String](s).asInstanceOf[EntityTask[String]]}: _*
			)
		)

	}
}

