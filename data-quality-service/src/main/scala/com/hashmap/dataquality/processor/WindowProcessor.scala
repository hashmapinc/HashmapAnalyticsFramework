package com.hashmap.dataquality.processor

import com.hashmap.dataquality.data.TelemetryData
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}

class WindowProcessor extends Processor[String, TelemetryData]{

  override def init(context: ProcessorContext): Unit = {

  }

  override def process(key: String, value: TelemetryData): Unit = {
    println(s"""\n---telemetryData-- $value \n""")
    // call to quality check service with key i.e deviceId and telemetryData
  }

  override def close(): Unit = {

  }
}
