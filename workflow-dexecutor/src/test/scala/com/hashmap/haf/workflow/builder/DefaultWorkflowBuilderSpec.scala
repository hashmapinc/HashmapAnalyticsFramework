package com.hashmap.haf.workflow.builder

import java.util.UUID

import com.github.dexecutor.core.task.Task
import com.hashmap.haf.workflow.models.Workflow
import com.hashmap.haf.workflow.task.SparkIgniteTask
import org.specs2.mutable.Specification

import scala.collection.immutable

class DefaultWorkflowBuilderSpec extends Specification {

	val builder = new DefaultWorkflowBuilder("test-spark-workflow.xml")

	"Workflow builder" should {
		"build a workflow" in {
			val workflow: Workflow[UUID, String] = builder.build()
			workflow.getName shouldEqual "Sample Workflow"
			workflow.getId must not be empty

		}

		"workflow should contain tasks" in {
			val workflow: Workflow[UUID, String] = builder.build()
			workflow.getTasks must not be empty
			workflow.getTasks.size shouldEqual 2
		}

		"workflow tasks should contain forked_deduplication task " in {
			val workflow: Workflow[UUID, String] = builder.build()
			val deduplicateTask = workflow.getTasks.head.asInstanceOf[SparkIgniteTask]
			deduplicateTask.name shouldEqual "forked_deduplication"
			deduplicateTask.id must not be empty
			deduplicateTask.inputCache shouldEqual "output_postgres"
			deduplicateTask.outputCache shouldEqual "output_deduplicate"
			deduplicateTask.functionArguments.size shouldEqual 1
			deduplicateTask.functionArguments("retainlast") shouldEqual "true"

			deduplicateTask.configurations("spark.driver.memory") shouldEqual "2g"
			deduplicateTask.configurations("spark.driver.cores") shouldEqual "1"
		}

		"workflow tasks should contain forked_summarize task " in {
			val workflow: Workflow[UUID, String] = builder.build()
			val forked_summarize = workflow.getTasks(1).asInstanceOf[SparkIgniteTask]
			forked_summarize.name shouldEqual "forked_summarize"
			forked_summarize.id must not be empty
			forked_summarize.inputCache shouldEqual "output_deduplicate"
			forked_summarize.outputCache shouldEqual "output_summarize"
			forked_summarize.functionArguments.size shouldEqual 1
			forked_summarize.functionArguments("aggregates") shouldEqual "min,max,avg"
			forked_summarize.configurations("spark.driver.memory") shouldEqual "1g"
			forked_summarize.configurations("spark.driver.cores") shouldEqual "1"
		}
	}

}
