package com.hashmap.haf.scheduler.datastore

import com.hashmap.haf.scheduler.consumer.rest.{WorkflowEvent, WorkflowEventImplicits}
import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository
import redis.RedisClient

import scala.concurrent.Future

class RedisWorkflowEventRepository(redis: RedisClient) extends WorkflowEventRepository {
  import WorkflowEventImplicits._

  import scala.concurrent.ExecutionContext.Implicits.global

  def addOrUpdate(workflowEvent : WorkflowEvent): Future[Boolean] = {
    redis.hmset(workflowEvent.id.toString, workflowEvent) // implicitly converting case class object to Map
  }

  def remove(workflowEventId : String): Future[Long]  = {
    redis.del(workflowEventId) //.hdel(workflowEvent.id.toString, workflowEvent.keySet.toList:_*)
  }

  override def getAll = {

    val ret: Future[Seq[WorkflowEvent]] =
      redis.keys("*")
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

  override def removeAll = {
    getAll.foreach(_.foreach(a => remove(a.id.toString)))
  }
}
