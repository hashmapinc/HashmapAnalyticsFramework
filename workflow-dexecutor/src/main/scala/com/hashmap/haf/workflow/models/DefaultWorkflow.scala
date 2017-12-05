package com.hashmap.haf.workflow.models

import java.util.UUID
import com.github.dexecutor.core.task.Task
import com.hashmap.haf.workflow.Workflow
import com.hashmap.haf.workflow.task.SparkTask
import scala.xml.Node

case class DefaultWorkflow(tasks: List[Task[UUID, String]], name: String)
	extends Workflow[UUID, String](tasks, name) {

}

object DefaultWorkflow{
	def apply(): DefaultWorkflow = new DefaultWorkflow(Nil, "EmptyWorkflow")


	def apply(xml: Node): DefaultWorkflow = {
		val workflowXml: Node = (xml \ "workflow").head
		new DefaultWorkflow(
			name = (workflowXml \ "name").text,
			tasks = List[Task[UUID, String]]((workflowXml \ "task").toList map { s => SparkTask(s) }: _*)
		)

	}
}

