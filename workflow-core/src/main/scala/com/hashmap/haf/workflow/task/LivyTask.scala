package com.hashmap.haf.workflow.task

import java.util.UUID

import com.hashmap.haf.workflow.constants.XmlConstants._
import com.hashmap.haf.workflow.util.UUIDConverter

import scala.xml.{Elem, NodeSeq}

case class LivyTask(override val name: String,
                    override val id: UUID = UUID.randomUUID(),
                    jar: String,
										className: String,
										inputCache: String,
										outputCache: String,
										functionArguments: Map[String, String],
										configurations: Map[String, String],
                    override val to: List[String] = List()) extends BaseTask[String](name, id, to){

	//@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
	//protected val mapSvc = _

	private val serialVersionUID = 5681660255884837805L

	def this(xml: NodeSeq) = this (
		name = (xml \ NAME_ATTRIBUTE).text,
		id = if ((xml \ ID_ATTRIBUTE).text != null && (xml \ ID_ATTRIBUTE).text.nonEmpty) UUIDConverter.fromString((xml \ ID_ATTRIBUTE).text) else UUID.randomUUID(),
		jar = (xml \ JAR_ATTRIBUTE).text,
		className = (xml \ CLASSNAME_ATTRIBUTE).text,
		inputCache = (xml \ LIVY_TASK \ INPUT_CACHE).text,
		outputCache = (xml \ LIVY_TASK \ OUTPUT_CACHE).text,
		functionArguments = (xml \ LIVY_TASK \ ARGS \ ARG).map(a => ((a \ KEY_ATTRIBUTE).text, a.text)).toMap,
		configurations = (xml \ LIVY_TASK \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
		to = (xml \ LIVY_TASK \ TO_TASK).map(a => (a \ TO_TASK_ATTRIBUTE).text).toList
	)


	override def execute(): String = {
		//mapSvc.runSurvice()
		???
	}

	override def toXml: Elem = {
		<task name={name} jar={jar} className={className} id={UUIDConverter.fromTimeUUID(id)}>
			<livy>
				<inputCache>{inputCache}</inputCache>
				<outputCache>{outputCache}</outputCache>
				{if (functionArguments.nonEmpty)
				<args>
					{
					functionArguments.map { a =>
						<arg key={a._1}>{a._2}</arg>
					}
					}
				</args>
				}
				{if (configurations.nonEmpty)
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
				{if(to.nonEmpty) {
				to.map { t =>
						<to task={t}/>
				}
			}
				}
			</livy>
		</task>
	}
}

object LivyTask {
	def apply(xml: NodeSeq): LivyTask = new LivyTask(xml)
}

