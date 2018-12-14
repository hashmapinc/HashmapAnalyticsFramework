package com.hashmap.dataquality.metadata

import scala.beans.BeanProperty

class TagMetaData {

  @BeanProperty
  var tag: String =_

  @BeanProperty
  var avgTagFrequency: String =_
}