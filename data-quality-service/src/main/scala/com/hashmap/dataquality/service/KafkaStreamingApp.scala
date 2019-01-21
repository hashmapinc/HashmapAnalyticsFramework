package com.hashmap.dataquality.service

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.processor.{TelemetryDataConsumer, WindowProcessor}
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import lombok.Getter
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import org.springframework.beans.factory.annotation.{Autowired, Qualifier, Value}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import scala.language.postfixOps


@Service
class KafkaStreamingApp {

  @Autowired @Qualifier("oauth2RestTemplate") @Getter
  private var oauth2RestTemplate: RestTemplate = _

  @Value("${kafka-app.kafka-bind-address}") val KAFKA_BIND_ADDRESS: String = ""

  @Value("${kafka-app.kafka-bind-port}") val KAFKA_BIND_PORT: String = ""

  @Value("${kafka-app.data-quality-topic}") val DATA_QUALITY_TOPIC: String = ""

  private val KAFKA_URL_FORMAT = "%s:%s"

  private val aggrValueStore = "aggregated-value-store"

  def run(): Unit = {
    val config = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "data-quality-service-app")
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "1")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, String.format(KAFKA_URL_FORMAT, KAFKA_BIND_ADDRESS, KAFKA_BIND_PORT))
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "com.hashmap.dataquality.serdes.TelemetryDataSerde")
    config.put("auto.offset.reset", "latest")

    val aggregatedValueStore = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(aggrValueStore),
      new Serdes.StringSerde, new TelemetryDataSerde)

    val builder = new Topology()
    
    builder.addSource("Source", DATA_QUALITY_TOPIC)
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
