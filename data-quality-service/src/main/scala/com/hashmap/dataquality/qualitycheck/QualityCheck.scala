package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.TelemetryData

trait QualityCheck {

  def check(deviceId: String, payload: TelemetryData)

}
