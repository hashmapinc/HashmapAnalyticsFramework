package com.hashmap.dataquality.streams

import akka.stream.scaladsl.Source
import com.hashmap.dataquality.data.Msgs.InboundMsg

trait SourceCreator[T] {
  def createSource():Source[(String, InboundMsg), T]
}
