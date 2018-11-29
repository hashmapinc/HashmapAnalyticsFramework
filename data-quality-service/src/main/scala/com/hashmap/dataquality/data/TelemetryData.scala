package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

case class TelemetryData(@JsonProperty("deviceId") deviceId: String,
                         @JsonProperty("ts") ts: Long,
                         @JsonProperty("tag") tag: String,
                         @JsonProperty("value") value: String)
