package com.hashmap.haf.workflow.builder

import com.hashmap.haf.workflow.Workflow

trait WorkflowBuilder[T <: Comparable[T], R] {
	def build(): Workflow[T, R]
}

abstract class ResourceWorkflowBuilder[T <:Comparable[T] , R](path: String) extends WorkflowBuilder[T, R] {
	protected def buildFromResource(): Workflow[T, R]

	override def build(): Workflow[T, R] = buildFromResource()
}
