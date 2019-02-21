package com.hashmap.dataquality.service

import com.hashmap.dataquality.streams.KafkaConsumerStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StreamStartService {

  @Value("${kinesis-support.enabled}") val KINESIS_ENABLED: Boolean = false

  def run(): Unit = {
    if(!KINESIS_ENABLED) {
      val kafkaConsumerStream = new KafkaConsumerStream
      kafkaConsumerStream.runGraph()
    }
  }
}
