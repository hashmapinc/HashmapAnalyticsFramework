package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.util.JsonUtil
import org.apache.kafka.common.serialization.Deserializer

class KafkaInboundMsgDeserializer extends Deserializer[KafkaInboundMsg]{
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {}

  override def close(): Unit = {}

  override def deserialize(s: String, bytes: Array[Byte]): KafkaInboundMsg = {
    try {
      return JsonUtil.fromJson[KafkaInboundMsg](new String(bytes))
    } catch {
      case _: Exception => print("Error ")
    }
    null
  }
}
