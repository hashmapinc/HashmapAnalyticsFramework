package com.hashmap.haf.scheduler.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.api.Scheduler
import com.hashmap.haf.scheduler.consumer.rest.WorkflowEvent
import com.hashmap.haf.scheduler.datastore.RedisWorkflowEventRepository
import com.hashmap.haf.scheduler.executor.actors.ExecutorActor
import com.hashmap.haf.scheduler.executor.impl.WorkflowExecutor
import redis.RedisClient

object SchedulerActor{
  def props(scheduler: Scheduler): Props = Props(new SchedulerActor(scheduler))
  final case class StartJob(workflowEvent: WorkflowEvent)
  final case class UpdateJob(_name: String, cronExpression: String)
  final case class SuspendJob(name: String)
  final case class RestartJob(name: String)
  final case class RemoveJob(name: String)
  final case object SuspendAll

}

class SchedulerActor(scheduler: Scheduler) extends Actor {
  import ExecutorActor._
  import SchedulerActor._

  import scala.concurrent.ExecutionContext.Implicits.global
  //val datastoreActor = system.actorOf(DatastoreActor.props(RedisWorkflowEventRepository))
  implicit val system = context.system
  val executorActor = system.actorOf(ExecutorActor.props(new WorkflowExecutor()))
  val redisWorkflowEventRepository = new RedisWorkflowEventRepository(RedisClient())
  override def receive = {
    case StartJob(workflowEvent) =>
      //Question : should we send a message to datastore actor instead ?
      redisWorkflowEventRepository.addOrUpdate(workflowEvent).foreach( _ => {
        scheduler.createJob(workflowEvent.id.toString, workflowEvent.cronExpression)
        scheduler.submitJob(workflowEvent.id.toString, executorActor , Execute(workflowEvent.id.toString))
      })
    case SuspendJob(id) =>
      redisWorkflowEventRepository.get(id)
        .map(we => redisWorkflowEventRepository.addOrUpdate(we.copy(isStarted = false)))
        .foreach(_ => scheduler.suspendJob(id))
    case RestartJob(id) => scheduler.resumeJob(id)
    case RemoveJob(id) => redisWorkflowEventRepository.remove(id).foreach(_ => scheduler.cancelJob(id))
    case UpdateJob(id, expr) => scheduler.updateJob(id, executorActor, expr, Execute(id))
  }

  override def postStop: Unit = {
    scheduler.SuspendAll
  }

  override def preStart(): Unit = {
    redisWorkflowEventRepository
      .getAll.foreach(_.foreach(we => self ! StartJob(we)))

  }
}
