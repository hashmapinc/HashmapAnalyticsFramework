package com.hashmap.dataquality.data

import scala.collection.mutable.ListBuffer

case class KinesisInboundMsg(deviceId: String, deviceName: String, tagList: ListBuffer[TsKvData])


