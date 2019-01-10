package com.hashmap.dataquality.metadata

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.AllArgsConstructor

@AllArgsConstructor
class DataQualityMetaData {
  @JsonProperty
  val token: String = null

  @JsonProperty
  val mandatoryTags: String = null
}
