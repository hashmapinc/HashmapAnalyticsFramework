package com.hashmap.haf.workflow.factory

import java.util.UUID
import com.github.dexecutor.core.task.Task
import com.hashmap.haf.workflow.task.SparkTask
import scala.language.higherKinds
import scala.xml.Node

object Factory {

	trait WorkflowTask[T <: Comparable[T], R] extends Task[T, R]

	trait TaskFactory[T <: Comparable[T], R] {
		def make(xml: Node): WorkflowTask[T, R]
	}

	object TaskFactory {
		def apply[T <: Comparable[T], R](xml: Node)(implicit ev: TaskFactory[T, R]): Task[T, R] = ev.make(xml)
	}

	implicit object EntityTaskFactory extends TaskFactory[UUID, String] {
		def make(xml: Node): WorkflowTask[UUID, String] = {
			(xml \ "_").headOption.map(_.label) match {
				case Some("spark") => SparkTask(xml)
				case _ => throw new IllegalArgumentException("No factory method found for given task")
			}
		}
	}

	/*//Example for other types
	implicit object IntTaskFactory extends TaskFactory[Int, String]{
		override def make(xml: Node): WorkflowTask[Int, String] = ???
	}*/

}