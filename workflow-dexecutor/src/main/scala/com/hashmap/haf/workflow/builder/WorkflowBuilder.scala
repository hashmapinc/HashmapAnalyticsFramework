package com.hashmap.haf.workflow.builder

import com.hashmap.haf.workflow.constants.XmlConstants._
import com.hashmap.haf.workflow.models.Workflow

import scala.xml.{Elem, Source, XML}

trait WorkflowBuilder[T <: Comparable[T], R] {
	def build(): Workflow[T, R]
}

abstract class ResourceWorkflowBuilder[T <:Comparable[T] , R](path: String) extends WorkflowBuilder[T, R] {
	private[builder] def buildFromResource(): Workflow[T, R] = {
		val source = Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream(path))
		val xml: Elem = XML.load(source)
		val commonConfigs = (xml \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap
		newWorkflow(xml, commonConfigs)
	}

	protected def newWorkflow(xml: Elem, commonConfigs: Map[String, String]): Workflow[T, R]

	override def build(): Workflow[T, R] = buildFromResource()
}
