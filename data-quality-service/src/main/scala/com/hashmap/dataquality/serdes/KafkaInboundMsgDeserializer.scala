package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.InboundMsg
import com.hashmap.dataquality.util.JsonUtil
import org.apache.kafka.common.serialization.Deserializer

class KafkaInboundMsgDeserializer extends Deserializer[InboundMsg]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {}

  override def close(): Unit = {}

  override def deserialize(s: String, bytes: Array[Byte]): InboundMsg = {
    try {
      println("inbound msg " + new String(bytes))
      return JsonUtil.fromJson[InboundMsg](new String(bytes))
    } catch {
      case _: Exception => print("Error ")
    }
    null
  }
}
