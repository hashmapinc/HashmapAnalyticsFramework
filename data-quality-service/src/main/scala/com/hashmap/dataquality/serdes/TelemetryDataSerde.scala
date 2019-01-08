package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.KafkaInboundMsg
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class TelemetryDataSerde extends Serde[KafkaInboundMsg]{

  val telemetryDataSerializer = new KafkaInboundMsgSerializer
  val telemetryDataDeserializer = new KafkaInboundMsgDeserializer

  override def deserializer(): Deserializer[KafkaInboundMsg] = {
     telemetryDataDeserializer
  }

  override def serializer(): Serializer[KafkaInboundMsg] = {
     telemetryDataSerializer
  }

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {
    telemetryDataSerializer.configure(configs, isKey)
    telemetryDataDeserializer.configure(configs, isKey)
  }

  override def close(): Unit = {
    telemetryDataSerializer.close()
    telemetryDataDeserializer.close()
  }
}
