package com.hashmap.haf.workflow.model

import scala.beans.BeanProperty

case class SavedWorkflow(@BeanProperty id: String, @BeanProperty name: String){
  def this() = this("", "")
}
