package com.hashmap.haf.workflow.execution

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import com.github.dexecutor.core.task.{ExecutionResult, Task, TaskExecutionException}
import com.github.dexecutor.core.{DexecutorState, ExecutionEngine}
import com.hashmap.haf.workflow.closure.SparkTaskClosure
import org.apache.ignite.IgniteCompute
import org.apache.ignite.lang.{IgniteFuture, IgniteInClosure}

class IgniteSparkExecutionEngine[T <: Comparable[T], R](executorState: DexecutorState[T, R],
                                                        igniteCompute: IgniteCompute,
                                                        completionQueue: BlockingQueue[ExecutionResult[T, R]])
	extends ExecutionEngine [T, R] {

	def this(dexecutorState: DexecutorState[T, R], igniteCompute: IgniteCompute) {
		//checkNotNull(igniteCompute, "Executer Service should not be null")
		//checkNotNull(completionQueue, "BlockingQueue should not be null")
		this(dexecutorState, igniteCompute, new LinkedBlockingQueue[ExecutionResult[T, R]]())
	}

	override def submit(task: Task[T, R]): Unit = {
		//logger.debug("Received Task {}", task.getId)
		val result = igniteCompute.callAsync(task.asInstanceOf[SparkTaskClosure[T, R]])
		result.listen(newListener)
	}

	private def newListener: IgniteInClosure[IgniteFuture[ExecutionResult[T, R]]] = {
		new IgniteListener(e => completionQueue.add(e.get))
	}

	@throws[TaskExecutionException]
	override def processResult: ExecutionResult[T, R] = {
		try {
			val executionResult: ExecutionResult[T, R] = completionQueue.take
			if (executionResult.isSuccess)
				this.executorState.removeErrored(executionResult)
			else
				this.executorState.addErrored(executionResult)
			executionResult
		} catch {
			case e: InterruptedException =>
				throw new TaskExecutionException("Task interrupted")
		}
	}

	override def isDistributed = true

	override def isAnyTaskInError: Boolean = this.executorState.erroredCount > 0

}

class IgniteListener[T, R](f: (IgniteFuture[ExecutionResult[T, R]]) => Unit) extends IgniteInClosure[IgniteFuture[ExecutionResult[T, R]]]{
	override def apply(e: IgniteFuture[ExecutionResult[T, R]]): Unit = f(e)
}
