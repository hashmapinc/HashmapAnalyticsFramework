package com.hashmap.haf.workflow.model

import scala.beans.BeanProperty

case class SavedWorkflowWithXML(@BeanProperty id: String,
                                @BeanProperty name: String,
                                @BeanProperty xml: String){
  def this() = this("", "", "")
}
