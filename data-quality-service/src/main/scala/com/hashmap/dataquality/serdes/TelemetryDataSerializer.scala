package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.TelemetryData
import com.hashmap.dataquality.util.JsonUtil
import org.apache.kafka.common.serialization.Serializer

class TelemetryDataSerializer() extends Serializer[TelemetryData]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {

  }

  override def serialize(s: String, t: TelemetryData): Array[Byte] = {
    try {
      return JsonUtil.toJson(t).getBytes()
    } catch {
      case _: Exception => print("Error ")
    }
    Array.emptyByteArray
  }

  override def close(): Unit = {

  }

}
