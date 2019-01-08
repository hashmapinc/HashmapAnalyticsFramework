package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.KafkaInboundMsg

trait QualityCheck {

  def check(deviceId: String, payload: KafkaInboundMsg)

}
