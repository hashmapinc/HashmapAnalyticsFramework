package com.hashmap.haf.workflow.closure

import com.github.dexecutor.core.task.{ExecutionResult, Task}
import org.apache.ignite.lang.IgniteCallable
import scala.util.{Failure, Success, Try}

abstract class SparkTaskClosure[T <: Comparable[T], R]
	extends Task[T, R] with IgniteCallable[ExecutionResult[T, R]]{

	def defaultValue[U]: U = {
		class Default[U] {
			var default: U = _
		}
		new Default[U].default
	}

	@throws[Exception]
	override def call(): ExecutionResult[T, R] = {
		Try(execute()) match {
			case Success(r) => ExecutionResult.success(getId, r)
			case Failure(e) => ExecutionResult.errored(getId, defaultValue[R] : R, s"Error occurred ${e.getMessage}")
		}
	}


}
