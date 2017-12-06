package com.hashmap.haf.workflow.processor

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.github.dexecutor.core._
import com.github.dexecutor.ignite.IgniteDexecutorState
import com.hashmap.haf.workflow.builder.{DefaultWorkflowBuilder, WorkflowBuilder}
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.ignite.IgniteContext
import com.hashmap.haf.workflow.models.Workflow
import com.hashmap.haf.workflow.task.DefaultTaskProvider
import org.apache.ignite.Ignite

class WorkflowProcessor(builder: WorkflowBuilder[UUID, String]) {

	val ignite: Ignite = IgniteContext.ignite

	def process(): Unit ={
		val workflow: Workflow[UUID, String] = builder.build()
		val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(workflow)
		workflow.buildTaskGraph(executor)
		executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
	}

	private def newTaskExecutor(workflow: Workflow[UUID, String]) = {
		val executorState = new IgniteDexecutorState[UUID, String](workflow.getId.toString, ignite)
		val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](executorState, ignite.compute), DefaultTaskProvider(workflow))
		config.setDexecutorState(executorState)
		new DefaultDexecutor[UUID, String](config)
	}
}

object WorkflowProcessor{
	def apply(path: String): WorkflowProcessor = new WorkflowProcessor(new DefaultWorkflowBuilder(path))
}
