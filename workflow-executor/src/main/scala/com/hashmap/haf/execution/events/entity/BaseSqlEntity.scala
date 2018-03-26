package com.hashmap.haf.execution.events.entity

import java.util.UUID

import com.hashmap.haf.workflow.util.UUIDConverter
import javax.persistence.{Column, Id, MappedSuperclass}

@MappedSuperclass
abstract class BaseSqlEntity[D] extends BaseEntity[D]{

  @Id
  @Column(name = "id")
  protected var id: String = _


  override def getId: UUID = {
    if (id == null) return null
    UUIDConverter.fromString(id)
  }

  override def setId(id: UUID) = {
    this.id = UUIDConverter.fromTimeUUID(id)
  }

  protected def toUUID(src: String): UUID = UUIDConverter.fromString(src)

  protected def toString(timeUUID: UUID): String = UUIDConverter.fromTimeUUID(timeUUID)

}

