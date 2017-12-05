package com.hashmap.haf.workflow.task

import java.util.UUID
import com.hashmap.haf.workflow.closure.SparkTaskClosure

abstract class EntityTask[R](val name: String,
                             val id: UUID = UUID.randomUUID(),
                             val to: Option[String] = None) extends SparkTaskClosure[UUID, R]{

	override def getId: UUID = id
}
