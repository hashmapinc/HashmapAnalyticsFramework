package com.hashmap.haf.scheduler.datastore.api

import com.hashmap.haf.scheduler.model.WorkflowEvent

import scala.concurrent.Future

trait WorkflowEventRepository {
  def addOrUpdate(workflowEvent : WorkflowEvent): Future[Boolean]

  def get(workflowEventId: String): Future[WorkflowEvent]
  def get(workflowEventId: List[String]): Future[List[WorkflowEvent]]
  def getAll: Future[Seq[WorkflowEvent]]

  def remove(workflowEventId : String): Future[Long]
  def removeAll: Unit
}