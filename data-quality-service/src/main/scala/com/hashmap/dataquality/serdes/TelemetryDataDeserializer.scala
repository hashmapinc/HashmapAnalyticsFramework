package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.TelemetryData
import com.hashmap.dataquality.util.JsonUtil
import org.apache.kafka.common.serialization.Deserializer

class TelemetryDataDeserializer extends Deserializer[TelemetryData]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {}

  override def close(): Unit = {}

  override def deserialize(s: String, bytes: Array[Byte]): TelemetryData = {
    try {
      return JsonUtil.fromJson[TelemetryData](new String(bytes))
    } catch {
      case _: Exception => print("Error ")
    }
    null
  }
}
