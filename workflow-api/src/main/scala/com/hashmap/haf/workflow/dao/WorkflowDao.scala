package com.hashmap.haf.workflow.dao

import java.util.UUID

import com.hashmap.haf.workflow.models.Workflow


trait WorkflowDao {
  def saveOrUpdate(workflow: Workflow[UUID, String]): Workflow[UUID, String]

  def findById(id: UUID): Workflow[UUID, String]

  def findAll: List[Workflow[UUID, String]]

  def deleteById(id: UUID)
}
