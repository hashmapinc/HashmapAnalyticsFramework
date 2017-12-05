package com.hashmap.haf.workflow.builder

import com.hashmap.haf.workflow.Workflow
import scala.xml.{Elem, Source, XML}

trait WorkflowBuilder[T <: Comparable[T], R] {
	def build(): Workflow[T, R]
}

abstract class ResourceWorkflowBuilder[T <:Comparable[T] , R](path: String) extends WorkflowBuilder[T, R] {
	private[builder] def buildFromResource(): Workflow[T, R] = {
		val source = Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(path))
		val xml: Elem = XML.load(source)
		newWorkflow(xml)
	}

	protected def newWorkflow(xml: Elem): Workflow[T, R]

	override def build(): Workflow[T, R] = buildFromResource()
}
