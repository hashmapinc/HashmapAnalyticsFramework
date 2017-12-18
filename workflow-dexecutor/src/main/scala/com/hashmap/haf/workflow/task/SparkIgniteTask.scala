package com.hashmap.haf.workflow.task

import java.util.UUID

import com.hashmap.haf.workflow.util.UUIDConverter

import scala.xml.{Elem, NodeSeq}

case class SparkIgniteTask(override val name: String,
                           override val id: UUID = UUID.randomUUID(),
                           className: String,
                           inputCache: String,
                           outputCache: String,
                           functionArguments: Map[String, String],
                           configurations: Map[String, String],
                           override val to: List[String] = Nil) extends BaseTask[String](name, id, to){

  //@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
  //protected val mapSvc = _

  override def execute(): String = {
    //mapSvc.runSurvice()
    ???
  }

  override def toXml: Elem = {
    <task name={name} className={className} id={UUIDConverter.fromTimeUUID(id)}>
      <spark>
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
      </spark>
    </task>
  }
}

object SparkIgniteTask {
  import com.hashmap.haf.workflow.constants.XmlConstants._

  def apply(xml: NodeSeq): SparkIgniteTask = {
    val idString = (xml \ ID_ATTRIBUTE).text
    new SparkIgniteTask(
      name = (xml \ NAME_ATTRIBUTE).text,
      id = if(idString != null && idString.nonEmpty) UUIDConverter.fromString(idString) else UUID.randomUUID(),
      className = (xml \ CLASSNAME_ATTRIBUTE).text,
      inputCache = (xml \ SPARK_TASK \ INPUT_CACHE).text,
      outputCache = (xml \ SPARK_TASK \ OUTPUT_CACHE).text,
      functionArguments = (xml \ SPARK_TASK \ ARGS \ ARG).map(a => ((a \ KEY_ATTRIBUTE).text, a.text)).toMap,
      configurations = (xml \ SPARK_TASK \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
      to = (xml \ SPARK_TASK \ TO_TASK).map(a =>(a \ TO_TASK_ATTRIBUTE).text).toList
    )
  }
}
