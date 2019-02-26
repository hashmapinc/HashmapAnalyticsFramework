package com.hashmap.dataquality.metadata

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.{AllArgsConstructor, NoArgsConstructor}

@AllArgsConstructor
@NoArgsConstructor
class DataQualityMetaData(@JsonProperty val token: String, @JsonProperty val metaData: String)
