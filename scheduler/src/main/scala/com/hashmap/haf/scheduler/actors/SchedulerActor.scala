package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, ActorSystem, Props}
import com.hashmap.haf.scheduler.api.Scheduler
import com.hashmap.haf.scheduler.datastore.api.WorkflowEventRepository
import com.hashmap.haf.scheduler.executor.actors.ExecutorActor
import com.hashmap.haf.scheduler.extension.SpringExtension
import com.hashmap.haf.scheduler.model.WorkflowEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

object SchedulerActor{
  def props(scheduler: Scheduler, system: ActorSystem, springExtension: SpringExtension): Props =
    Props(new SchedulerActor(scheduler, system, springExtension))

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

  val executorActor = system.actorOf(springExtension.props("executorActor"))

  @Autowired
  val workflowEventRepository: WorkflowEventRepository = null

  override def receive = {
    case StartJob(workflowEvent) =>
      //Question : should we send a message to datastore actor instead ?
      workflowEventRepository.addOrUpdate(workflowEvent).foreach(_ => {
        scheduler.createJob(workflowEvent.id, workflowEvent.cronExpression)
        scheduler.submitJob(workflowEvent.id, executorActor , Execute(workflowEvent.id))
      })

    case SuspendJob(id) =>
      workflowEventRepository.get(id)
        .map(we => workflowEventRepository.addOrUpdate(we.copy(isRunning = false)))
        .foreach(_ => scheduler.suspendJob(id))
    case RestartJob(id) => scheduler.resumeJob(id)
    case RemoveJob(id) => workflowEventRepository.remove(id).foreach(_ => scheduler.cancelJob(id))
    case UpdateJob(id, expr) => scheduler.updateJob(id, executorActor, expr, Execute(id))
  }

  override def postStop: Unit = {
    scheduler.SuspendAll
  }

  override def preStart(): Unit = {
    workflowEventRepository
      .getAll.foreach(_.foreach(we => self ! StartJob(we)))

  }
}
