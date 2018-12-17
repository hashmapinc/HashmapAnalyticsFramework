package com.hashmap.dataquality.processor

import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.config.KafkaAppConfig
import com.hashmap.dataquality.data.KafkaInboundMsg
import org.apache.kafka.streams.processor.{Processor, ProcessorContext, PunctuationType, Punctuator}
import org.apache.kafka.streams.state.KeyValueStore

class TelemetryDataConsumer extends Processor[String, KafkaInboundMsg]{

  private var context: ProcessorContext = _
  private var kvStore: KeyValueStore[String, KafkaInboundMsg] = _

  override def init(context: ProcessorContext): Unit = {
    this.context = context
    // call this processor's punctuate() method every 10000 time units. This is the time window over which
    // aggregation is done.
    val timeWindowMs = ApplicationContextProvider.getApplicationContext.getBean(classOf[KafkaAppConfig]).TIME_WINDOW
    this.context.schedule(timeWindowMs, PunctuationType.WALL_CLOCK_TIME, new PuncutatorImp)

    kvStore = context.getStateStore("aggregated-value-store").asInstanceOf[KeyValueStore[String, KafkaInboundMsg]]
  }

  override def process(key: String, value: KafkaInboundMsg): Unit = {
    if (kvStore.get(key) == null) {
      kvStore.put(key, value)
    } else {
      var tagList = kvStore.get(key).tagList
      tagList ++= value.tagList.toList
      kvStore.put(key, KafkaInboundMsg(kvStore.get(key).deviceName, tagList))
    }
  }

  class PuncutatorImp extends Punctuator {
    override def punctuate(timestamp: Long): Unit = {
      val iter = kvStore.all
      while ( {
        iter.hasNext
      }) {
        val entry = iter.next
        context.forward(entry.key, entry.value)
        kvStore.delete(entry.key)
      }
      iter.close()
      context.commit()
    }
  }

  override def close(): Unit = {

  }
}
