package com.hashmap.haf.workflow.processor

import java.util.UUID
import com.github.dexecutor.core.{DefaultDexecutor, DexecutorConfig}
import com.github.dexecutor.ignite.IgniteDexecutorState
import com.hashmap.haf.workflow.execution.IgniteSparkExecutionEngine
import com.hashmap.haf.workflow.ignite.IgniteContext
import com.hashmap.haf.workflow.task.DefaultTaskProvider
import org.apache.ignite.Ignite

object WorkflowProcessor {

	val ignite: Ignite = IgniteContext.ignite

	def process(): Unit ={
		val executor: DefaultDexecutor[UUID, String] = newTaskExecutor(UUID.randomUUID())
	}

	private def newTaskExecutor(workflowId: UUID) = {
		val dexecutorState = new IgniteDexecutorState[UUID, String](workflowId.toString, ignite)
		val config = new DexecutorConfig[UUID, String](new IgniteSparkExecutionEngine[UUID, String](dexecutorState, ignite.compute), DefaultTaskProvider())
		config.setDexecutorState(dexecutorState)
		new DefaultDexecutor[UUID, String](config)
	}
}
