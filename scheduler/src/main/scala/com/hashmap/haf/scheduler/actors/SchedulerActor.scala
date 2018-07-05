package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import com.hashmap.haf.scheduler.api.Scheduler
import com.hashmap.haf.scheduler.datastore.actors.DatastoreActor.{AddEvent, GetAll, GetEvent, RemoveEvent}
import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository
import com.hashmap.haf.scheduler.executor.actors.ExecutorActor
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.Future

object SchedulerActor{
  def props(scheduler: Scheduler, system: ActorSystem, springExtension: SpringExtension): Props =
    Props(new SchedulerActor(scheduler, system, springExtension))

  final case class CreateJob(workflowEvent: WorkflowEvent)
  final case class StartJob(workflowEvent: WorkflowEvent)
  final case class UpdateJob(_name: String, cronExpression: String)
  final case class SuspendJob(name: String)
  final case class RestartJob(name: String)
  final case class RemoveJob(name: String)
  final case object SuspendAll

}

@Component("schedulerActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class SchedulerActor @Autowired()(scheduler: Scheduler, system: ActorSystem, springExtension: SpringExtension) extends Actor {
  import ExecutorActor._
  import SchedulerActor._

  import scala.concurrent.ExecutionContext.Implicits.global

  val datastoreActor = system.actorOf(springExtension.props("datastoreActor"))
  val executorActor = system.actorOf(springExtension.props("executorActor"))

  //@Autowired
  //val workflowEventRepository: WorkflowEventRepository = null

  override def receive = {
    case CreateJob(workflowEvent: WorkflowEvent) =>
      scheduler.createJob(workflowEvent.id, workflowEvent.cronExpression)
      if(workflowEvent.isRunning){
        scheduler.submitJob(workflowEvent.id, executorActor , Execute(workflowEvent.id))
      }
      datastoreActor ! AddEvent(workflowEvent)
    case StartJob(workflowEvent) =>
      scheduler.submitJob(workflowEvent.id, executorActor , Execute(workflowEvent.id))
    case SuspendJob(id) =>
      scheduler.suspendJob(id)
      implicit val timeout: Timeout = Timeout(20 seconds)
      (datastoreActor ? GetEvent(id))
        .mapTo[WorkflowEvent]
        .map(we => datastoreActor ! AddEvent(we))

    case RestartJob(id) => scheduler.resumeJob(id)
    case RemoveJob(id) => //workflowEventRepository.remove(id).foreach(_ => scheduler.cancelJob(id))
      scheduler.cancelJob(id)
      datastoreActor ! RemoveEvent(id)
    case UpdateJob(id, expr) => ??? //scheduler.updateJob(id, executorActor, expr, Execute(id))
  }

  override def postStop: Unit = {
    scheduler.SuspendAll
  }

  override def preStart(): Unit = {
    implicit val timeout: Timeout = Timeout(20 seconds)
    (datastoreActor ? GetAll).mapTo[List[WorkflowEvent]]
      .foreach(_.foreach(we => self ! StartJob(we)))
//    workflowEventRepository
//      .getAll.foreach(_.foreach(we => self ! StartJob(we)))

  }
}
