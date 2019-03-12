package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

import scala.collection.mutable.ListBuffer

object Msgs {
  case class DeviceEvent(server: String, issue: String, value: String)
  case class InboundMsg(@JsonProperty("deviceId") deviceId: Option[String], @JsonProperty("deviceName") deviceName: String,
                        @JsonProperty("tagList") tagList: ListBuffer[TsKvData])
  case class ToActorMsg(deviceId: String, inboundMsg: InboundMsg)
  case class TsKvData(@JsonProperty("ts") ts: Long, @JsonProperty("tag") tag: String, @JsonProperty("value") value: String)
}
