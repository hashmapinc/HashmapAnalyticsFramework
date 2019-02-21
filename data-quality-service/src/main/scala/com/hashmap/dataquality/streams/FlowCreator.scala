package com.hashmap.dataquality.streams

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.hashmap.dataquality.data.{KafkaInboundMsg, ToActorMsg}

import scala.collection.immutable
import scala.concurrent.duration._

trait FlowCreator {
  protected def createFlow(): Flow[(String, KafkaInboundMsg), immutable.Seq[ToActorMsg], NotUsed] = {
    Flow[(String, KafkaInboundMsg)].groupedWithin(1000, 60 second).map(entry => entry.seq.map(x => ToActorMsg(x._1, x._2)))
  }
}
