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
  final case class StartJob(name: String, cronExpression: String)
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
    case StartJob(id, expr) =>
      //Question : should we send a message to datastore actor instead ?
      redisWorkflowEventRepository.addOrUpdate(WorkflowEvent(id.toLong, expr))
      scheduler.createJob(id, expr)
      scheduler.submitJob(id, executorActor , Execute(id))
    case SuspendJob(id) => scheduler.suspendJob(id)
    case RestartJob(id) => scheduler.resumeJob(id)
    case RemoveJob(id) =>
      redisWorkflowEventRepository.remove(id)
      scheduler.suspendJob(id)
    case UpdateJob(id, expr) => scheduler.updateJob(id, executorActor, expr, Execute(id))
  }

  override def postStop: Unit = {
    scheduler.SuspendAll
  }

  override def preStart(): Unit = {
    redisWorkflowEventRepository
      .getAll.foreach(_.foreach(we => self ! StartJob(we.id.toString, we.cronExpression)))

  }
}
