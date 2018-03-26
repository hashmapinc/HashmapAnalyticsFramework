package com.hashmap.haf.workflow.model

import scala.beans.BeanProperty

case class WorkflowTask(@BeanProperty taskName: String,
                        @BeanProperty inputCache: String,
                        @BeanProperty outputCache: String,
                        @BeanProperty toTaskName: String)
