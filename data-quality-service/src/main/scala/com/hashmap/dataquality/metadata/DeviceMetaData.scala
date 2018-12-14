package com.hashmap.dataquality.metadata

import com.fasterxml.jackson.databind.JsonNode

import scala.beans.BeanProperty

class DeviceMetaData {

  @BeanProperty
  var deviceId: String = _

  @BeanProperty
  var mandatoryTags: JsonNode = _
}
