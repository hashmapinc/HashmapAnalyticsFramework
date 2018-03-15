package com.hashmap.haf.execution.models

import java.util.UUID
import com.github.dexecutor.core.task.{ExecutionResult, ExecutionResults, ExecutionStatus}
import scala.collection.JavaConverters._

object Responses {

	case class WorkflowExecutionResult(id: String, status: ExecutionStatus,
																		 errors: WorkflowErrors, skippedTasks: List[UUID])

	object WorkflowExecutionResult{
		def apply(workflowId: String, executionResult: ExecutionResults[UUID, String]): WorkflowExecutionResult = {
			val results = executionResult.getAll.asScala.toList
			val errors = results.filter(_.isErrored).map(TaskError(_))
			val skipped = results.filter(_.isSkipped).map(_.getId)
			val status = (errors, skipped) match {
				case (Nil, Nil) => ExecutionStatus.SUCCESS
				case (Nil, _ :: _) => ExecutionStatus.SKIPPED
				case (_ :: _, _) => ExecutionStatus.ERRORED
			}
			WorkflowExecutionResult(workflowId, status, WorkflowErrors(errors), skipped)
		}
	}

	case class TaskError(id: UUID, error: String)
	object TaskError{
		def apply(result: ExecutionResult[UUID, String]): TaskError =
			new TaskError(result.getId, result.getMessage)
	}

	case class WorkflowErrors(taskErrors: List[TaskError])
}
