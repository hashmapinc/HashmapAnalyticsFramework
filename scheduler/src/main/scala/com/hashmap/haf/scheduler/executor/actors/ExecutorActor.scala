package com.hashmap.haf.scheduler.executor.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.executor.api.Executor

object ExecutorActor{
  def props(executor: Executor): Props = Props(new ExecutorActor(executor))
  final case class Execute(id: String)
  final case class GetStatus(id: Int)
}

class ExecutorActor(executor: Executor) extends Actor{
  import ExecutorActor._
  override def receive = {
    case Execute(a) => executor.execute(a)
    case GetStatus(id) => executor.status(id)
  }
}
