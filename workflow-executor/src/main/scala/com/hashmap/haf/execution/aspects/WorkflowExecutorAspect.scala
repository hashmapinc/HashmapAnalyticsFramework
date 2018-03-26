package com.hashmap.haf.execution.aspects

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import scala.util.{Failure, Success, Try}

@Aspect
@Component
class WorkflowExecutorAspect {

	private val logger = LoggerFactory.getLogger(classOf[WorkflowExecutorAspect])

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

}
