package com.hashmap.haf.scheduler.datastore.api

import com.hashmap.haf.scheduler.model.{Event, WorkflowEvent}

import scala.concurrent.Future

/*
* T represents Event Type
* */
trait EventRepository[T <: Event]{
  def addOrUpdate(event : T): Future[Boolean]
  def get(eventId: String): Future[Event]
  def get(eventIds: List[String]): Future[List[Event]]
  def getAll: Future[Seq[Event]]
  def remove(eventId : String): Future[Long]
  def removeAll: Unit
}


trait WorkflowEventRepository extends EventRepository[WorkflowEvent]

