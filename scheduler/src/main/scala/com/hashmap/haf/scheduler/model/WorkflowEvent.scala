package com.hashmap.haf.scheduler.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

import scala.beans.BeanProperty

@RedisHash("workflowEvents")
case class WorkflowEvent(@BeanProperty @Indexed @Id id: String,
                         @BeanProperty cronExpression: String,
                         @BeanProperty @Indexed isRunning: Boolean = false) extends Event {
  def this() = this("", "", false)
}

object WorkflowEventImplicits {
  implicit def workflowEventToMap(workflowEvent: WorkflowEvent): Map[String, String] = {
    Map("id" -> workflowEvent.id,
      "cronExpression" -> workflowEvent.cronExpression,
      "isRunning" -> workflowEvent.isRunning.toString)
  }
  implicit def mapToWorkflowEvent(storedMap: Map[String, String]): WorkflowEvent  = {
    WorkflowEvent(storedMap("id"), storedMap("cronExpression"), storedMap("isRunning").toBoolean)
  }
}
