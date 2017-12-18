package com.hashmap.haf.workflow.task

import java.util.UUID

import com.hashmap.haf.functions.services.ServiceFunction
import com.hashmap.haf.workflow.constants.XmlConstants._
import org.apache.ignite.resources.ServiceResource

import scala.xml.NodeSeq

case class SparkIgniteTask(override val name: String,
                           override val id: UUID = UUID.randomUUID(),
                           //function: String,
                           inputCache: String,
                           outputCache: String,
                           functionArguments: Map[String, String],
                           configurations: Map[String, String],
                           override val to: List[String] = Nil) extends BaseTask[String](name, id, to){


  def this(xml: NodeSeq, commonConfigs: Map[String, String]) = {
    this(
      name = (xml \ NAME_ATTRIBUTE).text,
      inputCache = (xml \ SPARK_TASK \ INPUT_CACHE).text,
      outputCache = (xml \ SPARK_TASK \ OUTPUT_CACHE).text,
      functionArguments = (xml \ SPARK_TASK \ ARGS \ ARG).map(a => ((a \ KEY_ATTRIBUTE).text, a.text)).toMap,
      configurations = commonConfigs ++ (xml \ SPARK_TASK \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
      to = (xml \ SPARK_TASK \ TO_TASK).map(a =>(a \ TO_TASK_ATTRIBUTE).text).toList
    )
  }

  //@ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[Nothing])
  //protected val mapSvc = _

  override def execute(): String = {
    //mapSvc.runSurvice()
    ???
  }
}

object SparkIgniteTask {
  import com.hashmap.haf.workflow.constants.XmlConstants._

  def apply(xml: NodeSeq, commonConfigs: Map[String, String]): SparkIgniteTask = {
   // (xml \ "@className").text
    new SparkIgniteTask(
      name = (xml \ NAME_ATTRIBUTE).text,
      inputCache = (xml \ SPARK_TASK \ INPUT_CACHE).text,
      outputCache = (xml \ SPARK_TASK \ OUTPUT_CACHE).text,
      functionArguments = (xml \ SPARK_TASK \ ARGS \ ARG).map(a => ((a \ KEY_ATTRIBUTE).text, a.text)).toMap,
      configurations = commonConfigs ++ (xml \ SPARK_TASK \ CONFIGURATIONS \ CONFIGURATION).map(n => ((n \ CONFIGURATION_KEY).text, (n \ CONFIGURATION_VALUE).text)).toMap,
      to = (xml \ SPARK_TASK \ TO_TASK).map(a =>(a \ TO_TASK_ATTRIBUTE).text).toList
    )
  }
}

case class DummyTask(xml: NodeSeq, commonConfigs: Map[String, String]) extends SparkIgniteTask(xml, commonConfigs){
  @ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = classOf[ServiceFunction])
  protected val mapSvc: ServiceFunction= _

  override def execute(): String = {
    mapSvc.run(inputCache, outputCache, configurations)
  }
}
