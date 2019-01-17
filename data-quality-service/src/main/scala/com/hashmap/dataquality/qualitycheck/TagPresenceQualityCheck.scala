package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{DeviceEvent, KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.MetadataService
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagPresenceQualityCheck @Autowired()(metadataFetchService: MetadataService,
                                           mqttConnector: MqttConnector) extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[TagPresenceQualityCheck])

  private val DEVICE_EVENT_TOPIC = "v1/gateway/events"
  private val MISSING_ELEMENTS = "missingElements"

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {
    log.info("Kafka Inbound Msg {}" + JsonUtil.toJson(payload))
    val tagsNotPresent = checkTagsPresence(deviceId, payload.tagList.toList)
    if (tagsNotPresent.nonEmpty) {
      publishAsTelemetry(payload.deviceName, tagsNotPresent)
      publishAsEvents(payload.deviceName, tagsNotPresent)
    }
  }

  private def publishAsEvents(deviceName: String, missingTags: List[String]): Unit = {
    try {
      val client = new MqttClient(mqttConnector.getMqttUrl, MqttClient.generateClientId, new MemoryPersistence)
      val options = new MqttConnectOptions
      options.setUserName(mqttConnector.getAccessToken)
      client.setTimeToWait(1000)
      client.connect(options)
      val event = DeviceEvent("data-quality-service", MISSING_ELEMENTS, JsonUtil.toJson(missingTags))
      client.publish(DEVICE_EVENT_TOPIC, JsonUtil.toJson(Map(deviceName -> event)).getBytes(), 0, false)
      client.close()
    } catch {
      case e: Exception => log.info("Exception " + e)
    }
  }

  private def publishAsTelemetry(deviceName: String, missingTags: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map(MISSING_ELEMENTS -> missingTags)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def checkTagsPresence(deviceId: String, tagsPresent: List[TsKvData]): List[String] = {
    val metaData = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(errorMsg) => log.error(s"""Missing metadata for $deviceId because "$errorMsg". Tag presence quality check failed"""); return List.empty
    }
    metaData.filter(mandatoryAttributes => !tagsPresent.map(_.tag).contains(mandatoryAttributes.tag)).map(_.tag)
  }

}
