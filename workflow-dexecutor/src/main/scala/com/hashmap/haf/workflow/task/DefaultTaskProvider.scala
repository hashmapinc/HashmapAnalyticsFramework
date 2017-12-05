package com.hashmap.haf.workflow.task

import java.util.UUID
import com.hashmap.haf.workflow.builder.WorkflowBuilder

class DefaultTaskProvider(taskBuilder: WorkflowBuilder[UUID, String])
	extends WorkflowTaskProvider[UUID, String](taskBuilder)

object DefaultTaskProvider {
	def apply(taskBuilder: WorkflowBuilder[UUID, String]): DefaultTaskProvider = new DefaultTaskProvider(taskBuilder)

	def apply(): DefaultTaskProvider = ???
}