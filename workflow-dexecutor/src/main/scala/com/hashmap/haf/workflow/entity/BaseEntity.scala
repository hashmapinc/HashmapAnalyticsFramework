package com.hashmap.haf.workflow.entity

import java.util.UUID


trait BaseEntity[D] extends ToData[D] with Serializable {
  def getId: UUID

  def setId(id: UUID)
}
