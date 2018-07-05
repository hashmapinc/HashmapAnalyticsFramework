package com.hashmap.haf.execution.events

import java.sql.Timestamp
import java.util.UUID
import com.fasterxml.jackson.databind.JsonNode

case class Event(id: UUID, target: String, eventTimestamp: Timestamp, eventInfo: JsonNode)

case class ExceptionWrapper(message: String, stackTrace: String)

sealed trait EventType{
  def value: String
}

case object WorkflowStarted extends EventType{
  override val value = "WORKFLOW_STARTED"
}

case object WorkflowCompleted extends EventType {
  override val value: String = "WORKFLOW_COMPLETED"
}

case object WorkflowFailed extends EventType {
  override val value: String = "WORKFLOW_FAILED"
}