package com.hashmap.haf.workflow.service

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow


trait WorkflowService {

  def saveWorkflow(workflowXml: String): Workflow[UUID, String]

}
