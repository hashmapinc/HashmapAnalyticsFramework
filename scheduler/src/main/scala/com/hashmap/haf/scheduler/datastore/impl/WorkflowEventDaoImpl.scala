package com.hashmap.haf.scheduler.datastore.impl

import com.hashmap.haf.scheduler.datastore.api.EventDao
import com.hashmap.haf.scheduler.datastore.repository.WorkflowEventRepository
import com.hashmap.haf.scheduler.model.{Event, WorkflowEvent}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.Future
import scala.util.Try
import scala.collection.JavaConversions._

@Component
class WorkflowEventDaoImpl @Autowired()(private val workflowEventRepository: WorkflowEventRepository) extends EventDao[WorkflowEvent] {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def addOrUpdate(event: WorkflowEvent): Future[Boolean] = Future {
    Try(workflowEventRepository.save[WorkflowEvent](event)).isSuccess
  }
  override def get(eventId: String): Future[Event] = Future {
    workflowEventRepository.findOne(eventId)
  }

  override def get(eventIds: List[String]): Future[List[Event]] = Future {
    workflowEventRepository.findAll(eventIds).toList
  }

  override def getAll: Future[Seq[Event]] = Future {
    workflowEventRepository.findAll().toList
  }

  override def remove(eventId: String): Future[Boolean] = Future {
    Try(workflowEventRepository.delete(eventId)).isSuccess
  }

  override def removeAll: Unit = workflowEventRepository.deleteAll()
}
