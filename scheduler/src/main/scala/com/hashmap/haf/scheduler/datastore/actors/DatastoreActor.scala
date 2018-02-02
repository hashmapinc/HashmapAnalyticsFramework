package com.hashmap.haf.scheduler.datastore.actors

import akka.actor.{Actor, ActorSystem, Props}
import com.hashmap.haf.scheduler.api.Scheduler
import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global


object DatastoreActor {
  def props(repository:  WorkflowEventRepository) = Props(new DatastoreActor(repository))
  case class AddEvent(workflowEvent: WorkflowEvent)
  case class GetEvent(workflowId: String)
  case class GetEvents(workflowId: List[String])
  case class RemoveEvent(workflowId: String)
  case object RemoveAll
  case object GetAll

}

@Component("datastoreActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class DatastoreActor @Autowired()(repository:  WorkflowEventRepository) extends Actor {
  import DatastoreActor._
  override def receive = {
    case AddEvent(we) => repository.addOrUpdate(we)
    case GetEvent(id) => repository.get(id) pipeTo sender
    case GetEvents(id) => repository.get(id) pipeTo sender
    case RemoveEvent(id) => repository.remove(id)
    case RemoveAll => repository.removeAll
    case GetAll => repository.getAll
  }
}
