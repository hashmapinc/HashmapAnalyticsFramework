package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

import scala.collection.mutable.ListBuffer

case class InboundMsg(@JsonProperty("deviceName") deviceName: String, @JsonProperty("tagList") tagList: ListBuffer[TsKvData])
