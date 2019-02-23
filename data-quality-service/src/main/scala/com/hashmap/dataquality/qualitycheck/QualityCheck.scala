package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.InboundMsg

trait QualityCheck {

  def check(deviceId: String, payload: InboundMsg)

}
