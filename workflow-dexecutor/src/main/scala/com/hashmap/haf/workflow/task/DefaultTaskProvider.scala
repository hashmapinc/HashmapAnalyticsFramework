package com.hashmap.haf.workflow.task

import java.util.UUID
import com.hashmap.haf.workflow.Workflow
import com.hashmap.haf.workflow.builder.{DefaultWorkflowBuilder, WorkflowBuilder}

class DefaultTaskProvider(workflow: Workflow[UUID, String])
	extends WorkflowTaskProvider[UUID, String](workflow){

	def this(builder: WorkflowBuilder[UUID, String]){
		this(builder.build())
	}
}

object DefaultTaskProvider {
	def apply(taskBuilder: WorkflowBuilder[UUID, String]): DefaultTaskProvider = new DefaultTaskProvider(taskBuilder)

	def apply(path: String): DefaultTaskProvider = new DefaultTaskProvider(new DefaultWorkflowBuilder(path))

	def apply(workflow: Workflow[UUID, String]): DefaultTaskProvider = new DefaultTaskProvider(workflow)
}