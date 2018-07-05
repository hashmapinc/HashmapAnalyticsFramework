package com.hashmap.haf.execution.events.entity

/**
  * The trait To dto.
  */
trait ToData[T] {

  /**
    * This method convert domain model object to data transfer object.
    *
    * @return the dto object
    */
  def toData(): T

}
