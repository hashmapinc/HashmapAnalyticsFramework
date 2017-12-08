package com.hashmap.haf.workflow.builder

import java.util.UUID

import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}

import scala.xml.Elem

class DefaultWorkflowBuilder(path: String) extends ResourceWorkflowBuilder[UUID, String](path) {
	override protected def newWorkflow(xml: Elem, commonConfigs: Map[String, String]): Workflow[UUID, String] = DefaultWorkflow(xml, commonConfigs)
}
