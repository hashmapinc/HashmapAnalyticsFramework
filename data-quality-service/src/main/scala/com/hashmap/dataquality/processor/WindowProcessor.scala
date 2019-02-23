package com.hashmap.dataquality.processor

import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.InboundMsg
import com.hashmap.dataquality.service.ActorService
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}

class WindowProcessor extends Processor[String, InboundMsg] {

  override def init(context: ProcessorContext): Unit = {

  }

  override def process(key: String, value: InboundMsg): Unit = {
    if(key != null && value != null)
      ApplicationContextProvider.getApplicationContext.getBean(classOf[ActorService]).process(key, value)
  }

  override def close(): Unit = {

  }

}
