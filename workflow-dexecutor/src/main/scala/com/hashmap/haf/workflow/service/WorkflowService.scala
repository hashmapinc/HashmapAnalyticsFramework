package com.hashmap.haf.workflow.service

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow


trait WorkflowService {

  def saveOrUpdate(workflowXml: String): Workflow[UUID, String]

  def findById(id: UUID): Workflow[UUID, String]

  def delete(id: UUID): Unit
}
