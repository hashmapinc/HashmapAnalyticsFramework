package com.hashmap.haf.workflow.dao

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow
import org.springframework.stereotype.Component

@Component
class WorkflowDaoImpl extends WorkflowDao {
  override def saveWorkflow(workflow: Workflow[UUID, String]): Workflow[UUID, String] = {
    workflow
  }
}
