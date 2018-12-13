package com.hashmap.dataquality.metadata

import scala.beans.BeanProperty

class DeviceMetaData {

  @BeanProperty
  var deviceId: String = _

  @BeanProperty
  var mandatoryTags: TagMetaData = _
}
