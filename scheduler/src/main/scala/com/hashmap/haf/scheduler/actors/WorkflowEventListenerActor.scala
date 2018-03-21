package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, ActorSystem, Props}
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

object WorkflowEventListenerActor {
  def props = Props[WorkflowEventListenerActor]

  final case class AddJob(workflowEvent: WorkflowEvent)
  final case class StopJob(workflowId: String)
  final case class DropJob(workflowId: String)
  final case class JobStatus(workflowId: String)
  final object RemoveAllJobs

}

@Component("workflowEventListenerActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class WorkflowEventListenerActor @Autowired()(system: ActorSystem, springExtension: SpringExtension) extends Actor {
  import SchedulerActor._
  import WorkflowEventListenerActor._

  val schedulerActor = system.actorOf(springExtension.props("schedulerActor"))

  override def receive = {
    case AddJob(workflowEvent) =>
      schedulerActor ! CreateJob(workflowEvent)
      //schedulerActor ! StartJob(workflowEvent)
    case StopJob(id) => schedulerActor ! SuspendJob(id)
    case DropJob(id) => schedulerActor ! RemoveJob(id)
    case JobStatus(_) => ???
  }
}
