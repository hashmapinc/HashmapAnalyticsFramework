package com.hashmap.haf.workflow.builder

import java.util.UUID

import com.github.dexecutor.core.task.Task
import com.hashmap.haf.workflow.models.Workflow
import com.hashmap.haf.workflow.task.SparkIgniteTask
import org.specs2.mutable.Specification

import scala.collection.immutable

class DefaultWorkflowBuilderSpec extends Specification {

	val builder = new DefaultWorkflowBuilder()
	val workflowFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<workflow name=\"Sample Workflow\">\n    <configurations>\n        <configuration>\n            <key>spark.driver.memory</key>\n            <value>1g</value>\n        </configuration>\n        <configuration>\n            <key>spark.driver.cores</key>\n            <value>1</value>\n        </configuration>\n    </configurations>\n    <task name = \"forked_edit_metadata\" className=\"com.hashmap.haf.tasks.MetaDataIgniteTask\">\n        <spark>\n            <inputCache>output_postgres</inputCache>\n            <outputCache>output_metadata</outputCache>\n            <args>\n                <arg key=\"someargs\">someargsvalues</arg>\n            </args>\n            <to task=\"forked_deduplication\"/>\n            <to task=\"forked_summarize\"/>\n        </spark>\n    </task>\n    <task name = \"forked_deduplication\" className=\"com.hashmap.haf.tasks.DeduplicateSparkIgniteTask\">\n        <spark>\n            <configurations>\n                <configuration>\n                    <key>spark.driver.memory</key>\n                    <value>2g</value>\n                </configuration>\n            </configurations>\n            <inputCache>output_metadata</inputCache>\n            <outputCache>output_deduplicate</outputCache>\n            <args>\n                <arg key=\"retainlast\">true</arg>\n            </args>\n        </spark>\n    </task>\n    <task name = \"forked_summarize\" className=\"com.hashmap.haf.tasks.SummarizeIgniteTask\">\n        <spark>\n            <inputCache>output_metadata</inputCache>\n            <outputCache>output_summarize</outputCache>\n            <args>\n                <arg key=\"aggregates\">min,max,avg</arg>\n            </args>\n            <to task=\"end\"/>\n        </spark>\n    </task>\n</workflow>\n\n        <!-- TODO: Think about jobs joining -->"

	"Workflow builder" should {
		"build a workflow" in {

			val workflow: Workflow[UUID, String] = builder.build(workflowFileContent)
			workflow.getName shouldEqual "Sample Workflow"
			workflow.getId must not be empty

		}

		"workflow should contain tasks" in {
			val workflow: Workflow[UUID, String] = builder.build(workflowFileContent)
			workflow.getTasks must not be empty
			workflow.getTasks.size shouldEqual 3
		}

		"workflow tasks should contain forked_deduplication task " in {
			val workflow: Workflow[UUID, String] = builder.build(workflowFileContent)
			val deduplicateTask = workflow.getTasks(1).asInstanceOf[SparkIgniteTask]
			deduplicateTask.name shouldEqual "forked_deduplication"
			deduplicateTask.id must not be empty
			deduplicateTask.inputCache shouldEqual "output_metadata"
			deduplicateTask.outputCache shouldEqual "output_deduplicate"
			deduplicateTask.functionArguments.size shouldEqual 1
			deduplicateTask.functionArguments("retainlast") shouldEqual "true"

			deduplicateTask.configurations("spark.driver.memory") shouldEqual "2g"
			deduplicateTask.configurations("spark.driver.cores") shouldEqual "1"
			deduplicateTask.to.size shouldEqual 0
		}

		"workflow tasks should contain forked_summarize task " in {
			val workflow: Workflow[UUID, String] = builder.build(workflowFileContent)
			val forked_summarize = workflow.getTasks(2).asInstanceOf[SparkIgniteTask]
			forked_summarize.name shouldEqual "forked_summarize"
			forked_summarize.id must not be empty
			forked_summarize.inputCache shouldEqual "output_metadata"
			forked_summarize.outputCache shouldEqual "output_summarize"
			forked_summarize.functionArguments.size shouldEqual 1
			forked_summarize.functionArguments("aggregates") shouldEqual "min,max,avg"
			forked_summarize.configurations("spark.driver.memory") shouldEqual "1g"
			forked_summarize.configurations("spark.driver.cores") shouldEqual "1"
			forked_summarize.to.size shouldEqual 1
		}

		"workflow tasks should contain forked_edit_metadata task " in {
			val workflow: Workflow[UUID, String] = builder.build(workflowFileContent)
			val forked_edit_metadata = workflow.getTasks(0).asInstanceOf[SparkIgniteTask]
			forked_edit_metadata.name shouldEqual "forked_edit_metadata"
			forked_edit_metadata.id must not be empty
			forked_edit_metadata.inputCache shouldEqual "output_postgres"
			forked_edit_metadata.outputCache shouldEqual "output_metadata"
			forked_edit_metadata.functionArguments.size shouldEqual 1
			forked_edit_metadata.functionArguments("someargs") shouldEqual "someargsvalues"
			forked_edit_metadata.configurations("spark.driver.memory") shouldEqual "1g"
			forked_edit_metadata.configurations("spark.driver.cores") shouldEqual "1"
			forked_edit_metadata.to.size shouldEqual 2
		}
	}
}
