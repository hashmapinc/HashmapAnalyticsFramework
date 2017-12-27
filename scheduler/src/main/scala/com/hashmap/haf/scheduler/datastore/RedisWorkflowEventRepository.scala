package com.hashmap.haf.scheduler.datastore

import akka.actor.ActorSystem
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

  def remove(workflowEvent : WorkflowEvent): Future[Long]  = {
    redis.hdel(workflowEvent.id.toString, workflowEvent.keySet.toList:_*)
  }

  override def getAll = {

    val e: Future[Seq[WorkflowEvent]] = redis.keys("*").flatMap(keys => {
      val a: Seq[Future[WorkflowEvent]] = keys.map(key =>{
        val b: Future[Map[String, String]] = redis.hgetall[String](key)
        val z: Future[WorkflowEvent] = b.map(m1 => {
          val we: WorkflowEvent = m1
          we
        })
        z
      })
      val x: Future[Seq[WorkflowEvent]] = Future.sequence(a)
      x
    })

    e
  }

  override def removeAll = {
    getAll.foreach(_.foreach(remove))
  }
}
