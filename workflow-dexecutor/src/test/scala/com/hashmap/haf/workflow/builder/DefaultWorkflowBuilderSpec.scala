package com.hashmap.haf.workflow.builder

import java.util.UUID
import com.hashmap.haf.workflow.models.Workflow
import org.specs2.mutable.Specification

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
			workflow.getTasks.size shouldEqual 1
		}
	}

}
