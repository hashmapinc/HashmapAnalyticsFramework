package com.hashmap.haf.execution.exceptions

import java.util.Date

import com.hashmap.haf.execution.exceptions.Exceptions.{FunctionNotFoundException, SourceCompilationException, SourceGenerationException, WorkflowNotFoundException}
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

object Advices {

	case class ErrorDetails(timestamp: Date, message: String, details: String)

	@ControllerAdvice
	@RestController
	class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
		@ExceptionHandler(Array(classOf[FunctionNotFoundException], classOf[WorkflowNotFoundException]))
		def handleNotFoundException(ex: Exception, request: WebRequest): ResponseEntity[ErrorDetails] = {
			val errorDetails = ErrorDetails(new Date(), ex.getMessage, request.getDescription(false))
			new ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
		}

		//TODO: Need to be selective with Exceptions
		@ExceptionHandler(Array(classOf[SourceGenerationException], classOf[SourceCompilationException], classOf[Exception]))
		def handleInternalServerError(ex: Exception, request: WebRequest): ResponseEntity[ErrorDetails] = {
			val errorDetails = ErrorDetails(new Date(), ex.getMessage, request.getDescription(false))
			new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
		}
	}

}
