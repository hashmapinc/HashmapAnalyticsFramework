package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

import scala.collection.mutable.ListBuffer

case class KafkaInboundMsg(@JsonProperty("deviceName") deviceName: String, @JsonProperty("tagList") tagList: ListBuffer[TsKvData])
