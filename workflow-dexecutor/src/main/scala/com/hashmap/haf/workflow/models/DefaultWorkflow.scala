package com.hashmap.haf.workflow.models

import java.util.UUID
import com.github.dexecutor.core.Dexecutor
import com.hashmap.haf.workflow.constants.XmlConstants
import com.hashmap.haf.workflow.task.BaseTask
import com.hashmap.haf.workflow.factory.Factory._
import scala.xml.Node

case class DefaultWorkflow(tasks: List[BaseTask[String]], name: String, id: UUID = UUID.randomUUID())
	extends Workflow[UUID, String](tasks, name){

	override def getId: UUID = id

	def buildTaskGraph(executor: Dexecutor[UUID, String]): Unit = {
		tasks.foreach(t => {
			val toTasks = getEntityTasksForTasksStrings(t.to)
			toTasks match {
				case Nil => executor.addIndependent(t.id)
				case dts => dts.foreach(dt => executor.addDependency(t.getId, dt.getId))
			}
		})
	}

	def getEntityTasksForTasksStrings(ts: List[String]): List[BaseTask[String]] = {
		ts.filterNot(_.equalsIgnoreCase("end")).map(n =>
			tasks.find(_.name.equalsIgnoreCase(n)).getOrElse(throw new IllegalStateException("No valid to task defined"))
		)
	}
}

object DefaultWorkflow{
	import XmlConstants._

	def apply(): DefaultWorkflow = new DefaultWorkflow(Nil, "EmptyWorkflow")

	def apply(xml: Node, commonConfigs: Map[String, String]): DefaultWorkflow = {
		new DefaultWorkflow(
			name = (xml \ NAME_ATTRIBUTE).text,
			tasks = List[BaseTask[String]](
				(xml \ TASK).toList map {s => TaskFactory[UUID, String](s, commonConfigs).asInstanceOf[BaseTask[String]]}: _*
			)
		)

	}
}

