package com.hashmap.haf.execution.services

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dexecutor.core.task.ExecutionResults
import com.github.dexecutor.core.{DefaultDexecutor, DexecutorConfig, Duration, ExecutionConfig}
import com.hashmap.haf.execution.clients.FunctionsServiceClient
import com.hashmap.haf.execution.executor.IgniteDexecutorState
import com.hashmap.haf.execution.models.Responses.WorkflowExecutionResult
import com.hashmap.haf.functions.compiler.FunctionCompiler
import com.hashmap.haf.functions.processors.VelocitySourceGenerator
import com.hashmap.haf.models.IgniteFunctionType
import com.hashmap.haf.workflow.factory.Factory.{TaskFactory, WorkflowTask}
import com.hashmap.haf.workflow.task.{DefaultTaskProvider, SparkIgniteTask}
import org.apache.ignite.Ignite
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.hashmap.haf.workflow.constants.XmlConstants._
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.models.{DefaultWorkflow, Workflow}

import scala.xml.{Node, NodeSeq}

@Service
class WorkflowExecutionService @Autowired()(functionsServiceClient: FunctionsServiceClient,
															 sourceGenerator: VelocitySourceGenerator,
															 functionCompiler: FunctionCompiler,
															 ignite: Ignite) {

	private val logger = LoggerFactory.getLogger(classOf[WorkflowExecutionService])

	def executeWorkflow(workflowId: String, workflowXML: String): WorkflowExecutionResult ={
		val workflow = DefaultWorkflow(workflowXML, new CustomTaskFactory(workflowId))
		val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(workflow)
		workflow.buildTaskGraph(executor)
		val results: ExecutionResults[UUID, String] =
			executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
		WorkflowExecutionResult(workflowId, results)
	}

	private def newTaskExecutor(workflow: Workflow[UUID, String]) = {
		val executorState = new IgniteDexecutorState[UUID, String](workflow.getId.toString, ignite)
		val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](executorState), DefaultTaskProvider(workflow))
		config.setDexecutorState(executorState)
		new DefaultDexecutor[UUID, String](config)
	}

	private def generateSourceAndCompile(functionClassName: String) = {
		logger.trace(s"Generating source for $functionClassName")
		val functionTypeString = functionsServiceClient.getFunction(functionClassName.substring(functionClassName.lastIndexOf(".")+1))
		logger.debug(s"Received function $functionTypeString from functions-api")
		val mapper = new ObjectMapper()
		val functionType = mapper.readValue(functionTypeString, classOf[IgniteFunctionType])
		sourceGenerator.generateSource(functionType) match {
			case Right(source) =>
				logger.debug(s"Compiling source for $functionClassName")
				functionCompiler.compile(functionClassName, source)
				functionCompiler.loadClazz(functionClassName)

			case Left(_) => None
		}
	}

	class CustomTaskFactory(workflowId: String) extends TaskFactory[UUID, String] {
		def create(xml: Node): WorkflowTask[UUID, String] = {
			(xml \ "_").headOption.map(_.label) match {
				case Some(SPARK_TASK) | Some(LIVY_TASK) => {
					val functionClassName = (xml \ CLASSNAME_ATTRIBUTE).text
					functionCompiler.loadClazz(functionClassName) match {
						case Some(c) =>
							logger.debug(s"Compiler found already compiled class for $functionClassName")
							c.getConstructor(classOf[NodeSeq], classOf[Ignite], classOf[String]).newInstance(xml, ignite, workflowId).asInstanceOf[SparkIgniteTask]

						case _ =>
							logger.debug(s"No precompiled class found for $functionClassName.")
							generateSourceAndCompile(functionClassName).get.getConstructor(classOf[NodeSeq], classOf[Ignite], classOf[String]).newInstance(xml, ignite, workflowId).asInstanceOf[SparkIgniteTask]

					}
				}
				case None => throw new IllegalStateException("At least one tak should be defined in a workflow")
				case _ => throw new IllegalArgumentException("No factory method found for given task")
			}
		}
	}

}
