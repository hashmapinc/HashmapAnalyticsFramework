package com.hashmap.dataquality.streams

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import com.hashmap.dataquality.data.InboundMsg
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import com.hashmap.dataquality.service.StreamsService
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaConsumerStream extends StreamsService[Consumer.Control] {

  private val KAFKA_URL_FORMAT = "%s:%s"

  override def createSource(): Source[(String, InboundMsg), Consumer.Control] = {

    val config: Config = system.settings.config.getConfig("akka.kafka.consumer")
    val bootstrapServers = String.format(KAFKA_URL_FORMAT, appConfig.KAFKA_BIND_ADDRESS, appConfig.KAFKA_BIND_PORT)

    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, (new TelemetryDataSerde).deserializer())
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")

    val plainSource: Source[ConsumerRecord[String, InboundMsg], Consumer.Control] =
      Consumer.plainSource(consumerSettings, Subscriptions.topics(appConfig.DATA_QUALITY_TOPIC)) // dq-topic

    plainSource.map(entry => (entry.key(), entry.value()))
  }
}

