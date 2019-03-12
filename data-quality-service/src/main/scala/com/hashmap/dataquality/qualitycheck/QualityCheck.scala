package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.Msgs.InboundMsg

trait QualityCheck {

  def check(deviceId: String, payload: InboundMsg)

}
