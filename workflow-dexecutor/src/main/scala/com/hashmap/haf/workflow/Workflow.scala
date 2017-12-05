package com.hashmap.haf.workflow

import java.util.UUID
import com.github.dexecutor.core.task.Task

abstract class Workflow[T <: Comparable[T], R](tasks: List[Task[T, R]], name: String, id: UUID) {

	def this(tasks: List[Task[T, R]], name: String) {
		this(tasks, name, UUID.randomUUID())
	}

	def task(t: T): Task[T, R] = tasks.find(_.getId.equals(t)).getOrElse(throw new RuntimeException())
}
