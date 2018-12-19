package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.MetadataFetchService
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagPresenceQualityCheck @Autowired()(metadataFetchService: MetadataFetchService,
                                           mqttConnector: MqttConnector) extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[TagPresenceQualityCheck])

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {
    System.out.println("Kafka Inbound Msg {}" + JsonUtil.toJson(payload))
    val tagsNotPresent = checkTagsPresence(deviceId, payload.tagList.toList)
    if (tagsNotPresent.nonEmpty) {
      publish(payload.deviceName, tagsNotPresent)
    }
  }

  private def publish(deviceName: String, missingTags: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map("missingElements" -> missingTags)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def checkTagsPresence(deviceId: String, tagsPresent: List[TsKvData]): List[String] = {
    val qualityAttributes = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(_) => log.error(s"Missing metadata for $deviceId. Tag presence quality check failed"); return List.empty
    }
    qualityAttributes.filter(mandatoryAttributes => !tagsPresent.map(_.tag).contains(mandatoryAttributes.tag)).map(_.tag)
  }

}
