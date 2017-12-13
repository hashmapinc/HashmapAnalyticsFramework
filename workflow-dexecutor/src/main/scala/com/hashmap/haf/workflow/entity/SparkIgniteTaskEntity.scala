package com.hashmap.haf.workflow.entity

import java.util.UUID

import com.hashmap.haf.workflow.task.SparkIgniteTask
import com.hashmap.haf.workflow.util.UUIDConverter


class SparkIgniteTaskEntity(
                             id: UUID,
                             name: String,
                             inputCache: String,
                             outputCache: String,
                             functionArguments: Map[String, String],
                             configurations: Map[String, String],
                             to: List[String]
                           ) extends BaseSqlEntity[SparkIgniteTask](UUIDConverter.fromTimeUUID(id)) {

  override def toData(): SparkIgniteTask = {
    new SparkIgniteTask(name, id, inputCache, outputCache, functionArguments, configurations, to)
  }
}

object SparkIgniteTaskEntity {
  def apply(sparkIgniteTask: SparkIgniteTask): SparkIgniteTaskEntity =
    new SparkIgniteTaskEntity(sparkIgniteTask.id,
      sparkIgniteTask.name,
      sparkIgniteTask.inputCache,
      sparkIgniteTask.outputCache,
      sparkIgniteTask.functionArguments,
      sparkIgniteTask.configurations,
      sparkIgniteTask.to
    )
}
