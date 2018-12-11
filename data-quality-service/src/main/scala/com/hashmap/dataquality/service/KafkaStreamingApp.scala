package com.hashmap.dataquality.service

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.processor.{TelemetryDataConsumer, WindowProcessor}
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import lombok.Getter
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import scalaj.http.HttpResponse

import scala.language.postfixOps


@Service
class KafkaStreamingApp {

  @Autowired @Qualifier("oauth2RestTemplate") @Getter
  private var oauth2RestTemplate: RestTemplate = null

  def run(): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-quality-service-app")
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "1")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "com.hashmap.dataquality.serdes.TelemetryDataSerde")
    config.put("auto.offset.reset", "latest")

    val aggregatedValueStore = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("aggregated-value-store"),
      new Serdes.StringSerde, new TelemetryDataSerde)

//    val response = oauth2RestTemplate.getForObject("http://localhost:8080/api/08b88e70-f26d-11e8-9384-836123f6f05f/attribute/mandatory-tags"
//      , classOf[String])
//    println(s"---- $response")

    val builder = new Topology()

    // add the source processor node that takes Kafka topic "source-topic" as input

    builder.addSource("Source", "data-quality-topic")
      .addProcessor("Consumer-Processor",
        new ProcessorSupplier[String, KafkaInboundMsg]() {
          override def get = new TelemetryDataConsumer()
        }, "Source")

    builder.addProcessor("Window-Processor",
        new ProcessorSupplier[String, KafkaInboundMsg]() {
          override def get = new WindowProcessor()
        }, "Consumer-Processor")

    builder.addStateStore(aggregatedValueStore, "Consumer-Processor")

    val streams = new KafkaStreams(builder, config)

    streams.start()

    sys.ShutdownHookThread {
      streams.close(10, TimeUnit.SECONDS)
    }
  }

}
