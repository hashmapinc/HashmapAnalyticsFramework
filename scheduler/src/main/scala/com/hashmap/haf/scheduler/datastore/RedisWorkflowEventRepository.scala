package com.hashmap.haf.scheduler.datastore

import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository
import com.hashmap.haf.scheduler.model.WorkflowEvent
import com.hashmap.haf.scheduler.model.WorkflowEventImplicits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import redis.RedisClient

import scala.concurrent.Future

@Repository
class RedisWorkflowEventRepository @Autowired()(redis: RedisClient) extends WorkflowEventRepository {
  import WorkflowEventImplicits._

  import scala.concurrent.ExecutionContext.Implicits.global

  private val RUNNING_EVENTS_KEY = "running_event_ids"
  private val STOPPED_EVENTS_KEY = "stopped_event_ids"

  override def addOrUpdate(workflowEvent : WorkflowEvent): Future[Boolean] = {
    val workflowEventId = workflowEvent.id.toString
    redis.exists(workflowEventId).flatMap {
      case true =>
        updateSecondaryIndex(workflowEventId)
          .flatMap(_ => redis.hmset(workflowEventId, workflowEvent))
      case false =>
        addSecondaryIndex(workflowEventId, workflowEvent.isRunning)
          .flatMap(_ => redis.hmset(workflowEventId, workflowEvent))

    }
  }

  override def remove(workflowEventId : String): Future[Long]  = {
    removeSecondaryIndex(workflowEventId).flatMap(_ => redis.del(workflowEventId))
  }

  override def getAll = getEventsByPattern("*")

  override def get(workflowEventId: String): Future[WorkflowEvent] = getEventsByPattern(workflowEventId).map(_.head)

  override def removeAll = {
    getAll.foreach(_.foreach(workflowEvent => remove(workflowEvent.id)))
  }

  private def addSecondaryIndex(workflowEventId: String, isRunnable: Boolean): Future[Long] = {
    if(isRunnable)
      redis.sadd(RUNNING_EVENTS_KEY, workflowEventId)
    else
      redis.sadd(STOPPED_EVENTS_KEY, workflowEventId)
  }

  private def removeSecondaryIndex(workflowEventId: String): Future[Long] = {
    redis.sismember(RUNNING_EVENTS_KEY, workflowEventId).flatMap {
      case true => redis.srem(RUNNING_EVENTS_KEY, workflowEventId)
      case false => redis.srem(STOPPED_EVENTS_KEY, workflowEventId)
    }
  }

  private def updateSecondaryIndex(workflowEventId: String): Future[Long] = {
    redis.sismember(RUNNING_EVENTS_KEY, workflowEventId).flatMap {
      case true =>
        redis.srem(RUNNING_EVENTS_KEY, workflowEventId)
        redis.sadd(STOPPED_EVENTS_KEY, workflowEventId)
      case false =>
        redis.srem(STOPPED_EVENTS_KEY, workflowEventId)
        redis.sadd(RUNNING_EVENTS_KEY, workflowEventId)
    }
  }

  private def getEventsByPattern(pattern: String) = {
    val ret: Future[Seq[WorkflowEvent]] =
      redis.keys(pattern)
        .flatMap(keys => {
          val res: Future[Seq[WorkflowEvent]] = Future.sequence(keys.map(key =>{
            val allEventsInMapFormat: Future[Map[String, String]] = redis.hgetall[String](key)
            val allEvents: Future[WorkflowEvent] =
              allEventsInMapFormat.map(m1 => {
                val we: WorkflowEvent = m1 // implicit conversion
                we
              })
            allEvents
          }))
          res
        })
    ret
  }

}
