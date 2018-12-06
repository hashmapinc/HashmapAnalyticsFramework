package com.hashmap.dataquality.processor

import com.hashmap.dataquality.data.TelemetryData
import org.apache.kafka.streams.processor.{Processor, ProcessorContext, PunctuationType, Punctuator}
import org.apache.kafka.streams.state.KeyValueStore

class TelemetryDataConsumer extends Processor[String, TelemetryData]{

  private var context: ProcessorContext = _
  private var kvStore: KeyValueStore[String, TelemetryData] = _

  override def init(context: ProcessorContext): Unit = {
    this.context = context
    // call this processor's punctuate() method every 10000 time units. This is the time window over which
    // aggregation is done.
    this.context.schedule(10000, PunctuationType.WALL_CLOCK_TIME, new PuncutatorImp)

    kvStore = context.getStateStore("aggregated-value-store").asInstanceOf[KeyValueStore[String, TelemetryData]]
  }

  override def process(key: String, value: TelemetryData): Unit = {
    println(s"""-----val $value-----""")
    if (kvStore.get(key) == null) {
      kvStore.put(key, value)
    } else {
      var tagList = kvStore.get(key).tagList
      tagList ++= value.tagList.toList
      kvStore.put(key, TelemetryData(tagList))
    }
  }

  class PuncutatorImp extends Punctuator {
    override def punctuate(timestamp: Long): Unit = {
      println("----Inside punctuator-----")
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
