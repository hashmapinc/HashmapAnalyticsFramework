package com.hashmap.haf.scheduler.executor.impl

import com.hashmap.haf.scheduler.executor.api.Executor

case class WorkflowExecutor(conf: Any) extends Executor {
  override def execute(id: String):Status = {
    println(s"Executing workdlow with $id") //0/30 0/1 * 1/1 * ? *
    200
  }

  override def status(id: Int) = ???
}