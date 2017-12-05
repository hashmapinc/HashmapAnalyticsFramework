package com.hashmap.haf.workflow.builder

import java.util.UUID
import com.hashmap.haf.workflow.Workflow
import com.hashmap.haf.workflow.models.DefaultWorkflow
import scala.xml.Elem

class DefaultWorkflowBuilder(path: String) extends ResourceWorkflowBuilder[UUID, String](path) {
	override protected def newWorkflow(xml: Elem): Workflow[UUID, String] = DefaultWorkflow(xml)
}
