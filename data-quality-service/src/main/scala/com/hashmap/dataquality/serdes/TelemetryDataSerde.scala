package com.hashmap.dataquality.serdes

import java.util

import com.hashmap.dataquality.data.TelemetryData
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class TelemetryDataSerde extends Serde[TelemetryData]{

  val telemetryDataSerializer = new TelemetryDataSerializer
  val telemetryDataDeserializer = new TelemetryDataDeserializer

  override def deserializer(): Deserializer[TelemetryData] = {
     telemetryDataDeserializer
  }

  override def serializer(): Serializer[TelemetryData] = {
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
