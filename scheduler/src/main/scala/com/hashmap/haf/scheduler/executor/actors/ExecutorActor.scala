package com.hashmap.haf.scheduler.executor.actors

import akka.actor.{Actor, Props}
import com.hashmap.haf.scheduler.executor.api.Executor
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value

object ExecutorActor{
  def props(executor: Executor): Props = Props(new ExecutorActor(executor))
  final case class Execute(id: String)
  final case class GetStatus(id: Int)
}

@Component("executorActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class ExecutorActor(executor: Executor) extends Actor{
  import ExecutorActor._

  @Value("${test.context}")
  var testContext: Boolean = false

  override def receive: Receive = {
    case Execute(a) => executor.execute(a)
      // For testing purpose, executing job for once, after that it will only log message
      if(testContext) context.become(receiveRemainingMessages)

    case GetStatus(id) => executor.status(id)
  }

  def receiveRemainingMessages: Receive = {
    case Execute(a) => println(s"Got execution reqeust for id: $a")
    case GetStatus(id) => executor.status(id)
  }

}
