package com.hashmap.dataquality.service

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import com.hashmap.dataquality.qualitycheck.QualityCheck
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class KafkaStreamingApp {

  case class TelemetryData(deviceId: String, tagList: Map[String, String])

  @Autowired
  private var qualityChecks: List[QualityCheck] = _

  def run(): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-quality-service-app")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

    val builder = new StreamsBuilder()
//    val inputData: KStream[Windowed[String], String] = builder.stream[Windowed[String], String]("data-quality-topic")
//
//    val ktable: ((String, String, Unit) => Unit) => KTable[Windowed[String], Unit] = inputData
//      .groupBy((key, value) => value)
//      .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(60000))).aggregate()



    // data processing .. validations and quality checks

//    inputData.foreach{case (key, value) => parseData(value)}

    val streams = new KafkaStreams(builder.build(), config)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }

  def parseData(data: String): Unit = {
    val mapper = new ObjectMapper()
    val dataObj = mapper.readValue(data, classOf[TelemetryData]);

    //val kafkaInboundMsg = new KafkaInboundMsg(new Map("", new Map("","")));
    //actorService.process()

  }


}
