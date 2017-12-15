package com.hashmap.haf.workflow.util

import java.util.UUID

object UUIDConverter {
  def fromString(src: String): UUID = UUID.fromString(src.substring(7, 15) + "-" + src.substring(3, 7) + "-1" + src.substring(0, 3) + "-" + src.substring(15, 19) + "-" + src.substring(19))

  def fromTimeUUID(src: UUID): String = {
    //if (src.version != 1) throw new IllegalArgumentException("Not a time UUID!")
    val str = src.toString
    str.substring(15, 18) + str.substring(9, 13) + str.substring(0, 8) + str.substring(19, 23) + str.substring(24)
  }

  def fromTimeUUIDs(uuids: List[UUID]): List[String] = {
    if (uuids == null)
      null
    else
      uuids.map(fromTimeUUID)
  }

}
