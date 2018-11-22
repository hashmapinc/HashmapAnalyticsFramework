package com.hashmap.dataquality.message

import com.hashmap.dataquality.data.TsKvEntry

import scala.collection.mutable.ListBuffer

case class DeviceTelemetryMsg(deviceTelemetryMsg: ListBuffer[TsKvEntry])
