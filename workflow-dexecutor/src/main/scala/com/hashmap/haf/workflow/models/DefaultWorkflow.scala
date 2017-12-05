package com.hashmap.haf.workflow.models

import java.util.UUID
import com.github.dexecutor.core.Dexecutor
import com.hashmap.haf.workflow.Workflow
import com.hashmap.haf.workflow.task.{EntityTask, SparkTask}
import scala.xml.Node

case class DefaultWorkflow(tasks: List[EntityTask[String]], name: String)
	extends Workflow[UUID, String](tasks, name) {

	val id: UUID = UUID.randomUUID()

	override def getId: UUID = id

	def buildTaskGraph(executor: Dexecutor[UUID, String]): Unit ={
		tasks.foreach(t => {
			val toTask: Option[EntityTask[String]] = t.to.map(n => tasks.find(_.name.equalsIgnoreCase(n)).getOrElse(throw new RuntimeException("No to task defined")))
			if(toTask.isDefined)
				executor.addDependency(t.id, toTask.get.id)
			else
				executor.addIndependent(t.id)
		})
	}
}

object DefaultWorkflow{
	def apply(): DefaultWorkflow = new DefaultWorkflow(Nil, "EmptyWorkflow")

	def apply(xml: Node): DefaultWorkflow = {
		val workflowXml: Node = (xml \ "workflow").head
		new DefaultWorkflow(
			name = (workflowXml \ "name").text,
			tasks = List[EntityTask[String]]((workflowXml \ "task").toList map { s => SparkTask(s) }: _*)
		)

	}
}

