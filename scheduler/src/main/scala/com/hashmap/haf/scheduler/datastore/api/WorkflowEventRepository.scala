package com.hashmap.haf.scheduler.datastore.api

import com.hashmap.haf.scheduler.consumer.rest.WorkflowEvent

import scala.concurrent.Future

trait WorkflowEventRepository {
  def addOrUpdate(workflowEvent : WorkflowEvent): Future[Boolean]

  def get(workflowEventId: String): Future[WorkflowEvent]
  def getAll: Future[Seq[WorkflowEvent]]

  def remove(workflowEventId : String): Future[Long]
  def removeAll: Unit
}