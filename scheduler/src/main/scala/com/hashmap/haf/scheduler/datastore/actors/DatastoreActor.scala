package com.hashmap.haf.scheduler.datastore.actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import com.hashmap.haf.scheduler.datastore.impl.WorkflowEventDaoImpl
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.ExecutionContext.Implicits.global


object DatastoreActor {
  def props(eventDao:  WorkflowEventDaoImpl) = Props(new DatastoreActor(eventDao))
  case class AddEvent(event: WorkflowEvent)
  case class GetEvent(id: String)
  case class GetEvents(ids: List[String])
  case class RemoveEvent(id: String)
  case object RemoveAll
  case object GetAll
}

@Component("datastoreActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class DatastoreActor @Autowired()(eventDao:  WorkflowEventDaoImpl) extends Actor {
  import DatastoreActor._
  override def receive = {
    case AddEvent(we) => eventDao.addOrUpdate(we)
    case GetEvent(id) => eventDao.get(id) pipeTo sender
    case GetEvents(id) => eventDao.get(id) pipeTo sender
    case RemoveEvent(id) => eventDao.remove(id)
    case RemoveAll => eventDao.removeAll
    case GetAll => eventDao.getAll pipeTo sender
  }
}
