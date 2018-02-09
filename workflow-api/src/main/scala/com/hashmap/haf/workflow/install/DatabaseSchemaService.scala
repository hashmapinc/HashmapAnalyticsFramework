package com.hashmap.haf.workflow.install

trait DatabaseSchemaService {

  @throws[Exception]
  def createDatabaseSchema(): Unit

}
