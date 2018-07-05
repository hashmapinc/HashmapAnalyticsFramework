package com.hashmap.haf.workflow.models

import java.util.UUID

import com.github.dexecutor.core.Dexecutor
import com.hashmap.haf.workflow.constants.XmlConstants
import com.hashmap.haf.workflow.task.BaseTask
import com.hashmap.haf.workflow.factory.Factory._
import com.hashmap.haf.workflow.util.UUIDConverter

import scala.xml.{Elem, XML}

case class DefaultWorkflow(
														tasks: List[BaseTask[String]],
														name: String,
														configurations: Map[String, String],
														id: UUID = UUID.randomUUID())
	extends Workflow[UUID, String](tasks, name){

	private val serialVersionUID = 4094695900150649481L

	override def getId: UUID = id

	def buildTaskGraph(executor: Dexecutor[UUID, String]): Unit = {
		tasks.foreach(t => {
			val toTasks = getEntityTasksForTasksStrings(t.to)
			toTasks match {
				case Nil => executor.addIndependent(t.id)
				case dts => dts.foreach(dt => executor.addDependency(t.getId, dt.getId))
			}
		})
	}

	override def toXml(): Elem = {
		//todo populate the tag names from constants
		<workflow name={name} id={UUIDConverter.fromTimeUUID(id)}>
			{
				if (configurations.nonEmpty)
					<configurations>
						{
							configurations.map { c =>
								<configuration>
									<key>{c._1}</key>
									<value>{c._2}</value>
								</configuration>
							}
						}
					</configurations>
			}
			{
				tasks.map(_.toXml)
			}
		</workflow>
	}

	def getEntityTasksForTasksStrings(ts: List[String]): List[BaseTask[String]] = {
		ts.filterNot(_.equalsIgnoreCase("end")).map(n =>
			tasks.find(_.name.equalsIgnoreCase(n)).getOrElse(throw new IllegalStateException("No valid to task defined"))
		)
	}
}

object DefaultWorkflow{
	import XmlConstants._

	def apply(): DefaultWorkflow = new DefaultWorkflow(Nil, "EmptyWorkflow", Map())

	def apply(xmlContent: String): DefaultWorkflow = {
		val xml = XML.loadString(xmlContent)
		val idString = (xml \ ID_ATTRIBUTE).text
		new DefaultWorkflow(
			name = (xml \ NAME_ATTRIBUTE).text,
			tasks = List[BaseTask[String]](
				(xml \ TASK).toList map {s => TaskFactory[UUID, String](s).asInstanceOf[BaseTask[String]]}: _*
			),
			configurations = (xml \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
			id = if(idString != null && idString.nonEmpty) UUIDConverter.fromString(idString) else UUID.randomUUID()
		)

	}


	def apply(xmlContent: String, ev: TaskFactory[UUID, String]): DefaultWorkflow = {
		val xml = XML.loadString(xmlContent)
		val idString = (xml \ ID_ATTRIBUTE).text
		new DefaultWorkflow(
			name = (xml \ NAME_ATTRIBUTE).text,
			tasks = List[BaseTask[String]](
				(xml \ TASK).toList map {s => TaskFactory[UUID, String](s)(ev).asInstanceOf[BaseTask[String]]}: _*
			),
			configurations = (xml \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
			id = if(idString != null && idString.nonEmpty) UUIDConverter.fromString(idString) else UUID.randomUUID()
		)

	}
}

