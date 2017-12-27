package com.hashmap.haf.workflow.task

import java.util.UUID

import com.hashmap.haf.workflow.builder.{DefaultWorkflowBuilder, WorkflowBuilder}
import com.hashmap.haf.workflow.models.Workflow

class DefaultTaskProvider(workflow: Workflow[UUID, String])
	extends WorkflowTaskProvider[UUID, String](workflow){

	def this(builder: WorkflowBuilder[UUID, String], xmlContent: String){
		this(builder.build(xmlContent))
	}
}

object DefaultTaskProvider {
	def apply(taskBuilder: WorkflowBuilder[UUID, String], xmlContent: String): DefaultTaskProvider = new DefaultTaskProvider(taskBuilder, xmlContent: String)

	def apply(xmlContent: String): DefaultTaskProvider = new DefaultTaskProvider(new DefaultWorkflowBuilder(), xmlContent)

	def apply(workflow: Workflow[UUID, String]): DefaultTaskProvider = new DefaultTaskProvider(workflow)
}