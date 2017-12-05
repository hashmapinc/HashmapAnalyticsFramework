package com.hashmap.haf.workflow.task

import scala.xml.NodeSeq

case class SparkTask() extends EntityTask[String]{

	override def execute(): String = ???
}

object SparkTask {

	def apply(xml: NodeSeq): SparkTask = new SparkTask()

}

