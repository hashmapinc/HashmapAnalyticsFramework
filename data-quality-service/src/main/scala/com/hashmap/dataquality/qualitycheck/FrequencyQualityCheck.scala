package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.{MetadataFetchService, TagMetaData}
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class FrequencyQualityCheck extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[FrequencyQualityCheck])

  @Autowired
  private val metadataFetchService: MetadataFetchService = null

  @Value("${tempus.mqtt-url}") private val MQTT_URL = ""

  @Value("${tempus.gateway-access-token}") private val ACCESS_TOKEN = ""

  private var mqttConnector: MqttConnector = _

  @PostConstruct
  def init(): Unit = {
    mqttConnector = new MqttConnector(MQTT_URL, ACCESS_TOKEN)
  }

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {
    val tagsWithMissedFrequency = checkTagsFrequency(deviceId, payload.tagList.toList)
    publish(payload.deviceName, tagsWithMissedFrequency)
  }

  private def publish(deviceName: String, tagsWithFrequencyMismatch: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map("frequencyMismatchElements" -> tagsWithFrequencyMismatch)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def checkTagsFrequency(deviceId: String, tagList: List[TsKvData]): List[String] = {
    val tagsMetadata = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(_) => log.error(s"Missing metadata for $deviceId. Tag frequency quality check failed"); return List.empty
    }
    tagsMetadata.filter(metadata => checkTagFrequencyMismatch(metadata, tagList)).map(_.tag)
  }

  private def checkTagFrequencyMismatch(metadata: TagMetaData, tagList: List[TsKvData]): Boolean = {
    val tagPresenceCount = tagList.map(_.tag).count(metadata.tag.equals(_))
    val timestampRange = tagList.map(_.ts)
    val expectedCount = ((timestampRange.max - timestampRange.min) / metadata.avgTagFrequency.toLong).floor
    tagPresenceCount < expectedCount
  }
}
