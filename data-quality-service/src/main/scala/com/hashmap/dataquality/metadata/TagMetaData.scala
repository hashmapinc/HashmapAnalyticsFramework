package com.hashmap.dataquality.metadata

import org.codehaus.jackson.annotate.JsonProperty

case class TagMetaData(@JsonProperty("tag") tag: String,
                       @JsonProperty("avgTagFrequency") avgTagFrequency: String)