package com.hashmap.haf.workflow.service
import java.util.UUID

import com.hashmap.haf.workflow.builder.WorkflowBuilder
import com.hashmap.haf.workflow.models.Workflow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WorkflowServiceImpl @Autowired()(private val workflowBuilder: WorkflowBuilder[UUID, String]) extends WorkflowService {

  override def saveWorkflow(workflowXml: String): Workflow[UUID, String] = {
    val workflow: Workflow[UUID, String] = workflowBuilder.build(workflowXml)
    workflow
  }
}
