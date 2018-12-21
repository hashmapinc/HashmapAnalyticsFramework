package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.{MetadataFetchService, TagMetaData}
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class FrequencyQualityCheck @Autowired()(metadataFetchService: MetadataFetchService,
                                         mqttConnector: MqttConnector,
                                         @Value("data-quality-frequency.threhold") missmatchThreshold: Long) extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[FrequencyQualityCheck])

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {
    val tagsWithMissedFrequency = tagsWithFrequencyMismatch(deviceId, payload.tagList.toList)
    if (tagsWithMissedFrequency.nonEmpty) {
      publish(payload.deviceName, tagsWithMissedFrequency)
    }
  }

  private def publish(deviceName: String, tagsWithFrequencyMismatch: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map("frequencyMismatchElements" -> tagsWithFrequencyMismatch)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def tagsWithFrequencyMismatch(deviceId: String, tagList: List[TsKvData]): List[String] = {
    val tagsMetadata = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(_) => log.error(s"Missing metadata for $deviceId. Tag frequency quality check failed"); return List.empty
    }
    tagsMetadata.filter(metadata => isFrequencyMismatch(metadata, tagList)).map(_.tag)
  }

  private def isFrequencyMismatch(metadata: TagMetaData, tagList: List[TsKvData]): Boolean = {
    val tagPresenceCount = tagList.map(_.tag).count(metadata.tag.equals(_))
    val timestampRange = tagList.map(_.ts)
    val timeWindow = timestampRange.max - timestampRange.min + 1
    val expectedCount = (timeWindow / metadata.avgTagFrequency.toLong).round

    assert(metadata.avgTagFrequency.toLong <= timeWindow)

    if (tagPresenceCount == expectedCount) {
      false
    } else if (tagPresenceCount <= expectedCount) {
      (((expectedCount.toFloat - tagPresenceCount.toFloat) / expectedCount.toFloat) * 100) > missmatchThreshold
    } else {
      (((tagPresenceCount.toFloat - expectedCount.toFloat) / tagPresenceCount.toFloat) * 100) > missmatchThreshold
    }
  }
}
