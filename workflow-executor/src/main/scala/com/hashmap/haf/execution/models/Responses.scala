package com.hashmap.haf.execution.models

import java.util.UUID
import com.github.dexecutor.core.task.{ExecutionResult, ExecutionResults, ExecutionStatus}
import scala.collection.JavaConverters._

object Responses {

	case class WorkflowExecutionResult(id: String, status: ExecutionStatus,
																		 errors: Option[WorkflowErrors], skippedTasks: Option[List[UUID]])

	object WorkflowExecutionResult{
		def apply(workflowId: String, executionResult: ExecutionResults[UUID, String]): WorkflowExecutionResult = {
			val results = executionResult.getAll.asScala.toList
			val errors = results.filter(_.isErrored).map(TaskError(_))
			val skipped = results.filter(_.isSkipped).map(_.getId)
			val (status, we, ws ) = (errors, skipped) match {
				case (Nil, Nil) => (ExecutionStatus.SUCCESS, None, None)
				case (Nil, _ :: _) => (ExecutionStatus.SKIPPED, None, Some(skipped))
				case (_ :: _, _) => (ExecutionStatus.ERRORED, Some(WorkflowErrors(errors)), None)
			}
			WorkflowExecutionResult(workflowId, status, we, ws)
		}
	}

	case class TaskError(id: UUID, error: String)
	object TaskError{
		def apply(result: ExecutionResult[UUID, String]): TaskError =
			new TaskError(result.getId, result.getMessage)
	}

	case class WorkflowErrors(taskErrors: List[TaskError])
}
