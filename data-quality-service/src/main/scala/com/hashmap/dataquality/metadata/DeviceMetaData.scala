package com.hashmap.dataquality.metadata

import scala.beans.BeanProperty

class DeviceMetaData {

  @BeanProperty
  val deviceId: String = null

  @BeanProperty
  val mandatoryTags: java.util.List[TagMetaData] = null
}
