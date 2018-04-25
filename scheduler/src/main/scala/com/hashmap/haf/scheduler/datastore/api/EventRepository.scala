package com.hashmap.haf.scheduler.datastore.api

import com.hashmap.haf.scheduler.model.Event

import scala.concurrent.Future

/*
* T represents Event Type
* */
trait EventRepository[T <: Event] {
  def addOrUpdate(event : T): Future[Boolean]
  def get(eventId: String): Future[T]
  def get(eventIds: List[String]): Future[List[T]]
  def getAll: Future[Seq[T]]
  def remove(eventId : String): Future[Long]
  def removeAll: Unit
}