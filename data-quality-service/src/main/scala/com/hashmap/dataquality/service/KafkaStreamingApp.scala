package com.hashmap.dataquality.service

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.hashmap.dataquality.data.TelemetryData
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{Consumed, TimeWindows}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.springframework.stereotype.Service


@Service
class KafkaStreamingApp {

  def run(): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-quality-service-app")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "com.hashmap.dataquality.serdes.TelemetryDataSerde")
    implicit val telemetryDataSerde: Serde[TelemetryData] = new TelemetryDataSerde;
    config.put("auto.offset.reset", "latest")

    val builder = new StreamsBuilder()
    implicit val consumer: Consumed[String, TelemetryData] = Consumed.`with`(Serdes.String(), telemetryDataSerde)
    val inputData: KStream[String, TelemetryData] = builder.stream("data-quality-topic")
    val ktable = inputData
      .groupByKey
      .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60000)))
      .reduce(reducer)

    val streams = new KafkaStreams(builder.build(), config)

    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }

  def reducer(data: TelemetryData, value: TelemetryData): TelemetryData = {
    println(s"""-----  Value ----- $value""")
    value
  }

}
