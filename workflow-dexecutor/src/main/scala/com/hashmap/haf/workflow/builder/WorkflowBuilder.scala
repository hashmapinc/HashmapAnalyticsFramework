package com.hashmap.haf.workflow.builder

import com.hashmap.haf.workflow.constants.XmlConstants._
import com.hashmap.haf.workflow.models.Workflow

import scala.xml.{Elem, Source, XML}

trait WorkflowBuilder[T <: Comparable[T], R] {
	def build(xmlContent: String): Workflow[T, R]
}

abstract class ResourceWorkflowBuilder[T <:Comparable[T] , R] extends WorkflowBuilder[T, R] {
	private[builder] def buildFromResource(xmlContent: String): Workflow[T, R] = {
		//val source = Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(path))

		//val xml: Elem = XML.load(source)
		val xml: Elem = XML.loadString(xmlContent)
		newWorkflow(xml)
	}

	protected def newWorkflow(xml: Elem): Workflow[T, R]

	override def build(xmlContent: String): Workflow[T, R] = buildFromResource(xmlContent)
}
