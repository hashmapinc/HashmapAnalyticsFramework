package com.hashmap.haf.workflow.dao

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow


trait WorkflowDao {
  def saveWorkflow(workflow: Workflow[UUID, String]): Workflow[UUID, String]
}
