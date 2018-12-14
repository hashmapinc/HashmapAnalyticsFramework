package com.hashmap.dataquality.metadata

import com.fasterxml.jackson.annotation.JsonCreator

import scala.beans.BeanProperty

@JsonCreator
class TagMetaData {

  @BeanProperty
  var tag: String =_

  @BeanProperty
  var avgTagFrequency: String =_
}