package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.util.JsonUtil
import org.apache.kafka.common.serialization.Serializer

class KafkaInboundMsgSerializer() extends Serializer[KafkaInboundMsg]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {

  }

  override def serialize(s: String, t: KafkaInboundMsg): Array[Byte] = {
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
