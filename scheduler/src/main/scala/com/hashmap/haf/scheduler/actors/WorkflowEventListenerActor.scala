package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.consumer.rest.EventConsumer.system
import com.hashmap.haf.scheduler.impl.QuartzScheduler

object WorkflowEventListenerActor {
  def props =
    Props[WorkflowEventListenerActor]

  final case class AddJob(workflowId: Long, cronExpression: String)
  final case class StopJob(workflowId: Long)
  final object RemoveAllJobs

}

class WorkflowEventListenerActor extends Actor {
  import SchedulerActor._
  import WorkflowEventListenerActor._
  val schedulerActor = system.actorOf(SchedulerActor.props(new QuartzScheduler(system)))
  override def receive = {
    case AddJob(id, expr) => schedulerActor ! StartJob(id.toString, expr)
    //case RemoveJob(id) => schedulerActor ! RemoveJob()
  }
}
