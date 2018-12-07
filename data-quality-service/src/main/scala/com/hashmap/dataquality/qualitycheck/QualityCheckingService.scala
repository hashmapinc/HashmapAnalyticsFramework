package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.TelemetryData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class QualityCheckingService {

  @Autowired
  private var qualityChecks: java.util.List[QualityCheck] = _

  def processForQualityChecks(deviceId: String, payload: TelemetryData): Unit = {
    qualityChecks.asScala.toList.foreach(_.check(deviceId, payload))
  }


}


