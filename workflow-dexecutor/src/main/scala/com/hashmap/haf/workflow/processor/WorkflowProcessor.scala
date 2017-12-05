package com.hashmap.haf.workflow.processor

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.github.dexecutor.core._
import com.github.dexecutor.ignite.IgniteDexecutorState
import com.hashmap.haf.workflow.builder.{DefaultWorkflowBuilder, WorkflowBuilder}
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.ignite.IgniteContext
import com.hashmap.haf.workflow.task.DefaultTaskProvider
import org.apache.ignite.Ignite

class WorkflowProcessor(builder: WorkflowBuilder[UUID, String]) {

	val ignite: Ignite = IgniteContext.ignite

	def process(): Unit ={
		val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(UUID.randomUUID())
		buildTaskGraph(executor)
		executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(2, TimeUnit.SECONDS)))
	}

	private def newTaskExecutor(workflowId: UUID) = {
		val dexecutorState = new IgniteDexecutorState[UUID, String](workflowId.toString, ignite)
		val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](dexecutorState, ignite.compute), DefaultTaskProvider())
		config.setDexecutorState(dexecutorState)
		new DefaultDexecutor[UUID, String](config)
	}

	def buildTaskGraph(executor: Dexecutor[UUID, String]): Unit ={
		val workflow = new DefaultWorkflowBuilder("sample-spark-workflow.xml").build()
	}
}

object WorkflowProcessor{
	def apply: WorkflowProcessor = new WorkflowProcessor()
}
