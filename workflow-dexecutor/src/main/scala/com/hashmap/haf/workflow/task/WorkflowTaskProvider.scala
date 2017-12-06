package com.hashmap.haf.workflow.task

import com.github.dexecutor.core.task.{Task, TaskProvider}
import com.hashmap.haf.workflow.builder.WorkflowBuilder
import com.hashmap.haf.workflow.models.Workflow

abstract class WorkflowTaskProvider[T <: Comparable[T], U](workflow: Workflow[T, U])
	extends TaskProvider[T, U]{

	def this(builder: WorkflowBuilder[T, U]){
		this(builder.build())
	}

	override def provideTask(t: T): Task[T, U] = workflow.task(t)

}
