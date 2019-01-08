package com.hashmap.dataquality.data

import org.codehaus.jackson.annotate.JsonProperty

case class TsKvData(@JsonProperty("ts") ts: Long,
                    @JsonProperty("tag") tag: String,
                    @JsonProperty("value") value: String)
