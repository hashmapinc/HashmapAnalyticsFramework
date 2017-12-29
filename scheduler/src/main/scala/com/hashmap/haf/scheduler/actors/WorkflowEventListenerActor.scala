package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.consumer.rest.WorkflowEvent
import com.hashmap.haf.scheduler.impl.QuartzScheduler

object WorkflowEventListenerActor {
  def props =
    Props[WorkflowEventListenerActor]

  final case class AddJob(workflowEvent: WorkflowEvent)
  final case class StopJob(workflowId: String)
  final case class DropJob(workflowId: String)
  final case class JobStatus(workflowId: String)
  final object RemoveAllJobs

}

class WorkflowEventListenerActor extends Actor {
  import SchedulerActor._
  import WorkflowEventListenerActor._
  implicit val system = context.system
  val schedulerActor = system.actorOf(SchedulerActor.props(new QuartzScheduler(system)))

  override def receive = {
    case AddJob(workflowEvent) => schedulerActor ! StartJob(workflowEvent)
    case StopJob(id) => schedulerActor ! SuspendJob(id)
    case DropJob(id) => schedulerActor ! RemoveJob(id)
    case JobStatus(id) => ???
  }
}
