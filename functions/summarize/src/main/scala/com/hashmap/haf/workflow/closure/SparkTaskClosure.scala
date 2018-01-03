package com.hashmap.haf.workflow.closure

import com.github.dexecutor.core.task.{ExecutionResult, Task}
import org.apache.ignite.lang.IgniteCallable

import scala.util.{Failure, Success, Try}

case class SparkTaskClosure[T <: Comparable[T], R](task: Task[T, R])
	extends IgniteCallable[ExecutionResult[T, R]]{

	def defaultValue[U]: U = {
		class Default[U] {
			var default: U = _
		}
		new Default[U].default
	}

	@throws[Exception]
	override def call(): ExecutionResult[T, R] = {
		Try(task.execute()) match {
			case Success(r) => ExecutionResult.success(task.getId, r)
			case Failure(e) => ExecutionResult.errored(task.getId, defaultValue[R] : R, s"Error occurred ${e.getMessage}")
		}
	}


}
