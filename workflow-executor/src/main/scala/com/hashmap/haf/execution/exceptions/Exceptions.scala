package com.hashmap.haf.execution.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

object Exceptions {

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Workflow with given id not found.")
	class WorkflowNotFoundException(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {
		def this(msg: String) {
			this(msg, null)
		}
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Function specified in workflow not found")
	class FunctionNotFoundException(msg: String, cause: Throwable) extends RuntimeException(msg, cause){
		def this(msg: String){
			this(msg, null)
		}
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "No compiled class found for given function")
	class SourceCompilationException(msg: String, cause: Throwable) extends RuntimeException(msg, cause){
		def this(msg: String){
			this(msg, null)
		}
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Exception while generating source for function")
	class SourceGenerationException(msg: String, cause: Throwable) extends RuntimeException(msg, cause){
		def this(msg: String){
			this(msg, null)
		}
	}
}
