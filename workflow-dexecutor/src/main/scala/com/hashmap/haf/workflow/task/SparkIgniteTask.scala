package com.hashmap.haf.workflow.task

import java.util.UUID

import scala.xml.NodeSeq

case class SparkIgniteTask(override val name: String,
                           override val id: UUID = UUID.randomUUID(),
                           //function: String,
                           inputCache: String,
                           outputCache: String,
                           functionArguments: Map[String, String],
                           configurations: Map[String, String],
                           override val to: Option[String] = None) extends EntityTask[String](name, id, to){

  //@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
  //protected val mapSvc = _

  override def execute(): String = {
    //mapSvc.runSurvice()
    ???
  }
}

object SparkIgniteTask {
  import com.hashmap.haf.workflow.constants.XmlConstants._

  def apply(xml: NodeSeq, commonConfigs: Map[String, String]): SparkIgniteTask = new SparkIgniteTask(
    name = (xml \ NAME_ATTRIBUTE).text,
    inputCache = (xml \ SPARK_TASK \ INPUT_CACHE).text,
    outputCache = (xml \ SPARK_TASK \ OUTPUT_CACHE).text,
    functionArguments = (xml \ SPARK_TASK \ ARGS \ ARG).map(a => ((a \ KEY_ATTRIBUTE).text, a.text)).toMap,
    configurations = commonConfigs ++ (xml \ SPARK_TASK \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap
  )
}
