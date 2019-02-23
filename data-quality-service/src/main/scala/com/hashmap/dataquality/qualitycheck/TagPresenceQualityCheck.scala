package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.MqttPublisher
import com.hashmap.dataquality.data.{InboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.MetadataService
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagPresenceQualityCheck @Autowired()(metadataFetchService: MetadataService,
                                           mqttConnector: MqttConnector) extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[TagPresenceQualityCheck])

  private val MISSING_ELEMENTS = "missingElements"

  override def check(deviceId: String, payload: InboundMsg): Unit = {
    log.info("Kafka Inbound Msg {}" + JsonUtil.toJson(payload))
    val tagsNotPresent = checkTagsPresence(deviceId, payload.tagList.toList)
    if (tagsNotPresent.nonEmpty) {
      MqttPublisher.publishAsTelemetry(payload.deviceName, tagsNotPresent, mqttConnector, MISSING_ELEMENTS)
      MqttPublisher.publishAsEvents(payload.deviceName, tagsNotPresent, mqttConnector, MISSING_ELEMENTS)
    }
  }

  private def checkTagsPresence(deviceId: String, tagsPresent: List[TsKvData]): List[String] = {
    val metaData = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(errorMsg) => log.error(s"""Missing metadata for $deviceId because "$errorMsg". Tag presence quality check failed"""); return List.empty
    }
    metaData.filter(mandatoryAttributes => !tagsPresent.map(_.tag).contains(mandatoryAttributes.tag)).map(_.tag)
  }

}
