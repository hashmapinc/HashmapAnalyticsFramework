package com.hashmap.dataquality.processor

import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.KafkaInboundMsg
import com.hashmap.dataquality.qualitycheck.QualityCheckingService
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}

class WindowProcessor extends Processor[String, KafkaInboundMsg] {

  override def init(context: ProcessorContext): Unit = {

  }

  override def process(key: String, value: KafkaInboundMsg): Unit = {
    ApplicationContextProvider.getApplicationContext.getBean(classOf[QualityCheckingService]).processForQualityChecks(key, value)
  }

  override def close(): Unit = {

  }

}
