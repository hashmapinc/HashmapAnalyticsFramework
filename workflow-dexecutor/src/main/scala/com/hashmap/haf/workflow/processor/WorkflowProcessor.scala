package com.hashmap.haf.workflow.processor

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.github.dexecutor.core._
import com.hashmap.haf.workflow.builder.{DefaultWorkflowBuilder, WorkflowBuilder}
import com.hashmap.haf.workflow.ignite.IgniteContext
import com.hashmap.haf.workflow.models.Workflow
import org.apache.ignite.Ignite

class WorkflowProcessor(builder: WorkflowBuilder[UUID, String]) {

	val ignite: Ignite = null

	def process(xmlContent: String): Unit ={
		val workflow: Workflow[UUID, String] = builder.build(xmlContent)
		val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(workflow)
		workflow.buildTaskGraph(executor)
		executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
	}

	private def newTaskExecutor(workflow: Workflow[UUID, String]) = {
		//todo will remove this class
		//val executorState = new IgniteDexecutorState[UUID, String](workflow.getId.toString, ignite)
		//val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](executorState, ignite.compute), DefaultTaskProvider(workflow))
		//config.setDexecutorState(executorState)
		null
	}
}

object WorkflowProcessor{
	def apply(): WorkflowProcessor = new WorkflowProcessor(new DefaultWorkflowBuilder())
}
