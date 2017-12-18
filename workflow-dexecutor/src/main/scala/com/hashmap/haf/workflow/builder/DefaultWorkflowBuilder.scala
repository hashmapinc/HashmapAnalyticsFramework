package com.hashmap.haf.workflow.builder

import java.util.UUID

import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}
import org.springframework.stereotype.Service

import scala.xml.Elem

@Service
class DefaultWorkflowBuilder extends ResourceWorkflowBuilder[UUID, String] {
	override protected def newWorkflow(xml: Elem) = DefaultWorkflow(xml)
}
