package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.MetadataFetchService
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class TagPresenceQualityCheck extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[TagPresenceQualityCheck])

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
    val tagPresence: Map[String, Boolean] = checkTagsPresence(deviceId, payload.tagList.toList)
    publish(payload.deviceName, tagPresence.filter(!_._2).keys.toList)
  }

  private def publish(deviceName: String, missingTags: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map("missingElements" -> missingTags)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def checkTagsPresence(deviceId: String, tagsPresent: List[TsKvData]): Map[String, Boolean] = {
    val qualityAttributes = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(_) => log.error(s"Missing metadata for $deviceId. Tag presence quality check failed"); return Map.empty
    }
    qualityAttributes.map(mandatoryAttributes => (mandatoryAttributes.tag, tagsPresent.map(_.tag).contains(mandatoryAttributes.tag))).toMap
  }

}
