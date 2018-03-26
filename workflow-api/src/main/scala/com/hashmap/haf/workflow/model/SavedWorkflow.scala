package com.hashmap.haf.workflow.model

import scala.beans.BeanProperty
import java.util

case class SavedWorkflow(@BeanProperty id: String,
                         @BeanProperty name: String,
                         @BeanProperty xml: String,
                         @BeanProperty tasks: util.List[WorkflowTask]) {

  def this() = this("", "", "", null)
}


