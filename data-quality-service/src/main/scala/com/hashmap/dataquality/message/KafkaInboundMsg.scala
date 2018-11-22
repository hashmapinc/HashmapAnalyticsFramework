package com.hashmap.dataquality.message
import com.hashmap.dataquality.data.TsKvEntry

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

case class KafkaInboundMsg(msg: Map[String, ListBuffer[TsKvEntry]])

