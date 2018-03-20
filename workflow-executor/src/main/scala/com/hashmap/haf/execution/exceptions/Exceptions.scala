package com.hashmap.haf.execution.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

object Exceptions {

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Workflow with given id not found.")
	class WorkflowNotFoundException extends RuntimeException
}
