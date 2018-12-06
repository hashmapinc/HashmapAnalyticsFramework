package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

import scala.collection.mutable.ListBuffer

case class TelemetryData(@JsonProperty("tagList") tagList: ListBuffer[TsKvData])
