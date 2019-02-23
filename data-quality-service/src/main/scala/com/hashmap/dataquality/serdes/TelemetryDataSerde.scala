package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.InboundMsg
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class TelemetryDataSerde extends Serde[InboundMsg]{

  val telemetryDataSerializer = new KafkaInboundMsgSerializer
  val telemetryDataDeserializer = new KafkaInboundMsgDeserializer

  override def deserializer(): Deserializer[InboundMsg] = {
     telemetryDataDeserializer
  }

  override def serializer(): Serializer[InboundMsg] = {
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
