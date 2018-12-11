package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.KafkaInboundMsg
import org.springframework.stereotype.Service

@Service
class FrequencyQualityCheck extends QualityCheck {

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {

  }
}
