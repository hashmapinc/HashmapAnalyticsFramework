package com.hashmap.haf.workflow.closure

import com.github.dexecutor.core.task.{ExecutionResult, ExecutionStatus, Task}
import org.apache.ignite.lang.IgniteCallable

abstract class SparkTaskClosure[T <: Comparable[T], R]
	extends Task[T, R] with IgniteCallable[ExecutionResult[T, R]]{

	@throws[Exception]
	override def call(): ExecutionResult[T, R] = {
		var r: R = _
		var status = ExecutionStatus.SUCCESS
		try
			r = this.execute
		catch {
			case e: Exception =>
				status = ExecutionStatus.ERRORED
				//logger.error("Error Execution Task # {}", task.getId, e)
		}
		new ExecutionResult[T, R](this.getId, r, status)
	}

}
