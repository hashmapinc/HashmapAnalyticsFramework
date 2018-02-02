package com.hashmap.haf.scheduler.executor.impl

import com.hashmap.haf.scheduler.executor.api.Executor
import org.springframework.stereotype.Service

@Service
case class WorkflowExecutor() extends Executor {
  override def execute(id: String):Status = {
    println(s"Executing workflow with id: $id") //0/30 0/1 * 1/1 * ? *
    200
  }

  override def status(id: Int) = ???
}