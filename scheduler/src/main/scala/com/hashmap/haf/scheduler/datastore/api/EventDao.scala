package com.hashmap.haf.scheduler.datastore.api


import com.hashmap.haf.scheduler.model.Event

import scala.concurrent.Future


trait EventDao[T <: Event] {
  def addOrUpdate(event : T): Future[Boolean]
  def get(eventId: String): Future[Event]
  def get(eventIds: List[String]): Future[List[Event]]
  def getAll: Future[Seq[Event]]
  def remove(eventId : String): Future[Boolean] //change type
  def removeAll: Unit
}


