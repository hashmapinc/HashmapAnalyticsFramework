package com.hashmap.haf.scheduler.datastore.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.consumer.rest.WorkflowEvent

import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository

object DatastoreActor {
  def props(repository:  WorkflowEventRepository) = Props(new DatastoreActor(repository))
  case class AddEvent(workflowEvent: WorkflowEvent)
  case class RemoveEvent(workflowEvent: WorkflowEvent)
  case object RemoveAll

}
class DatastoreActor(repository:  WorkflowEventRepository) extends Actor {
  import DatastoreActor._
  override def receive = {
    case AddEvent(we) => repository.addOrUpdate(we)
    case RemoveEvent(we) => repository.remove(we)
    case RemoveAll => repository.removeAll
  }
}
