package com.hashmap.dataquality.service

import com.hashmap.dataquality.streams.{KafkaConsumerStream, KinesisConsumerStream}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StreamStartService {

  @Value("${kinesis.enabled}") val KINESIS_SUPPORT_ENABLED: Boolean = false

  def run(): Unit = {
    if(KINESIS_SUPPORT_ENABLED) {
      val kinesisConsumerStream = new KinesisConsumerStream
      kinesisConsumerStream.runGraph()
    } else {
      val kafkaConsumerStream = new KafkaConsumerStream
      kafkaConsumerStream.runGraph()
    }
  }
}
