package com.hashmap.haf.scheduler.datastore.api

import com.hashmap.haf.scheduler.consumer.rest.WorkflowEvent

import scala.concurrent.Future

trait WorkflowEventRepository {
  def addOrUpdate(workflowEvent : WorkflowEvent): Future[Boolean]
  def remove(workflowEvent : WorkflowEvent): Future[Long]
  def getAll: Future[Seq[WorkflowEvent]]
  def removeAll: Unit
}