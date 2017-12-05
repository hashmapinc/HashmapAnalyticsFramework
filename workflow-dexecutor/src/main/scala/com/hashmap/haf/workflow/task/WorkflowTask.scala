package com.hashmap.haf.workflow.task

import com.github.dexecutor.core.task.Task

abstract class WorkflowTask[T <: Comparable[T], R] extends Task[T, R]{

}
