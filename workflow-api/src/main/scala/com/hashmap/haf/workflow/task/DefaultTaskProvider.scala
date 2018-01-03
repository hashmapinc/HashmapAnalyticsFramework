package com.hashmap.haf.workflow.task

import java.util.UUID
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}

class DefaultTaskProvider(workflow: Workflow[UUID, String]) extends WorkflowTaskProvider[UUID, String](workflow)

object DefaultTaskProvider {
	def apply(xmlContent: String): DefaultTaskProvider = new DefaultTaskProvider(DefaultWorkflow(xmlContent))

	def apply(workflow: Workflow[UUID, String]): DefaultTaskProvider = new DefaultTaskProvider(workflow)
}