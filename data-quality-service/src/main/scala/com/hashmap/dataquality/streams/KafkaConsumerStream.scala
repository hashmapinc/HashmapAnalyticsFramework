package com.hashmap.dataquality.streams

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaConsumerStream extends StreamsService[Consumer.Control] {

  override def createSource(): Source[(String, KafkaInboundMsg), Consumer.Control] = {

    val config: Config = system.settings.config.getConfig("akka.kafka.consumer")
    val bootstrapServers = "kafka:9092"

    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, (new TelemetryDataSerde).deserializer())
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")

    val plainSource: Source[ConsumerRecord[String, KafkaInboundMsg], Consumer.Control] =
      Consumer.plainSource(consumerSettings, Subscriptions.topics("dq-topic"))

    plainSource.map(entry => (entry.key(), entry.value()))
  }
}

