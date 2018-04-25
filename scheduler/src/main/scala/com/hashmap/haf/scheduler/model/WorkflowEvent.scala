package com.hashmap.haf.scheduler.model

import scala.beans.BeanProperty

case class WorkflowEvent(@BeanProperty id: String,
                         @BeanProperty cronExpression: String,
                         @BeanProperty isRunning: Boolean = false) extends Event {
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
