package com.hashmap.dataquality.data

import com.fasterxml.jackson.databind.JsonNode

import scala.beans.BeanProperty

class TagMetaData {

  @BeanProperty
  var deviceId: String = _

  @BeanProperty
  var mandatoryTags: JsonNode = _
}
