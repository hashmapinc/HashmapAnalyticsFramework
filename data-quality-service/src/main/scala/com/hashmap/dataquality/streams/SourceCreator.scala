package com.hashmap.dataquality.streams

import akka.stream.scaladsl.Source
import com.hashmap.dataquality.data.KafkaInboundMsg

trait SourceCreator[T] {
  def createSource():Source[(String, KafkaInboundMsg), T]
}
