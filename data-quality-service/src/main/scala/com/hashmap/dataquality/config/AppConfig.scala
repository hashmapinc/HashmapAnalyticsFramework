package com.hashmap.dataquality.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppConfig {
  @Value("${kafka-app.time-window}") val TIME_WINDOW: Long = 0
  @Value("${aws.access-key}") val ACCESS_KEY: String = ""
  @Value("${aws.secret-key}") val SECRET_KEY: String = ""
  @Value("${kinesis.stream-name}") val STREAM_NAME: String = ""
  @Value("${kinesis.shard-id}") val SHARD_ID: String = ""
  @Value("${kafka-app.kafka-bind-address}") val KAFKA_BIND_ADDRESS: String = ""
  @Value("${kafka-app.kafka-bind-port}") val KAFKA_BIND_PORT: String = ""
  @Value("${kafka-app.data-quality-topic}") val DATA_QUALITY_TOPIC: String = ""
}
