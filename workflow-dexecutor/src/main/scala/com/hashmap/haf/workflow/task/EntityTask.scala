package com.hashmap.haf.workflow.task

import java.util.UUID
import com.hashmap.haf.workflow.closure.SparkTaskClosure

abstract class EntityTask[R] extends SparkTaskClosure[UUID, R]{
	val id: UUID = entityId()

	protected def entityId(): UUID = UUID.randomUUID()

	override def getId: UUID = id
}
