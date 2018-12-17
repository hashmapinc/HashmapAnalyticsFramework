package com.hashmap.dataquality.metadata

import scala.beans.BeanProperty

case class DeviceMetaData (@BeanProperty deviceId: String, @BeanProperty mandatoryTags: java.util.List[TagMetaData])
