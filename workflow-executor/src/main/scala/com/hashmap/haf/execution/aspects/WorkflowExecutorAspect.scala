package com.hashmap.haf.execution.aspects

import java.sql.Timestamp
import java.util.UUID
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.hashmap.haf.execution.events.{Event, ExceptionWrapper}
import com.hashmap.haf.execution.events.publisher.EventsPublisher
import com.hashmap.haf.execution.models.Responses.WorkflowExecutionResult
import javax.annotation.PostConstruct
import org.apache.commons.lang3.exception.ExceptionUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

@Aspect
@Component
class WorkflowExecutorAspect @Autowired()(publisher: EventsPublisher) {

	private val logger = LoggerFactory.getLogger(classOf[WorkflowExecutorAspect])
	private val mapper = new ObjectMapper()

	@PostConstruct
	def init(): Unit ={
		mapper.registerModule(DefaultScalaModule)
	}

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	def logCallToEndpoint(joinPoint: ProceedingJoinPoint): Object = {
		val startTime = System.currentTimeMillis()
		logger.trace(s"Execution of $joinPoint started at $startTime")

		val value = Try(joinPoint.proceed()) match {
			case Success(o) => o
			case Failure(e) =>
				logger.trace(s"Execution of $joinPoint failed with exception ${e.getMessage}")
				throw e
		}

		val endTime = System.currentTimeMillis()

		logger.trace(s"Execution of $joinPoint took ${endTime - startTime} ms to complete")

		value
	}

	@Around("execution(* com.hashmap.haf.execution.services.WorkflowExecutionService.executeWorkflow(String, String)")
	def publishEvent(joinPoint: ProceedingJoinPoint): Object = {
		val startTime = System.currentTimeMillis()
		logger.trace(s"Execution of $joinPoint started at $startTime")
		val workflowId: String = joinPoint.getArgs()(0).asInstanceOf[String]

		val value = Try(joinPoint.proceed()) match {
			case Success(o) =>
				Future {
					publishWorkflowExecutionStatusEvent(startTime, workflowId, o)
				}
				o
			case Failure(e) =>
				logger.trace(s"Execution of $joinPoint failed with exception ${e.getMessage}")
				Future {
					val event = Event(UUID.randomUUID(), workflowId, new Timestamp(startTime),
						mapper.convertValue(ExceptionWrapper(e.getMessage, ExceptionUtils.getStackTrace(e)), classOf[JsonNode]))
					publisher.publish(event)
				}
				throw e
		}

		val endTime = System.currentTimeMillis()

		logger.trace(s"Execution of $joinPoint took ${endTime - startTime} ms to complete")

		value
	}

	private def publishWorkflowExecutionStatusEvent(startTime: Long, workflowId: String, o: AnyRef): Unit = {
		val event = Event(UUID.randomUUID(), workflowId, new Timestamp(startTime),
			mapper.convertValue(o.asInstanceOf[WorkflowExecutionResult], classOf[JsonNode]))
		publisher.publish(event)
	}
}
