package com.hashmap.haf.workflow.model

import scala.beans.BeanProperty

case class WorkflowTask(@BeanProperty taskName: String,
                        @BeanProperty inputCache: String,
                        @BeanProperty outputCache: String,
                        @BeanProperty toTaskName: String) extends Serializable {
  private val serialVersionUID = 6907745469068490450L
}
