package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.hashmap.dataquality.data.{DeviceEvent, KafkaInboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.{MetadataService, TagMetaData}
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class FrequencyQualityCheck @Autowired()(metadataFetchService: MetadataService,
                                         mqttConnector: MqttConnector,
                                         @Value("${data-quality-frequency.threhold}") missmatchThreshold: Long) extends QualityCheck {

  private val log = LoggerFactory.getLogger(classOf[FrequencyQualityCheck])

  private val DEVICE_EVENT_TOPIC = "v1/gateway/events"
  private val FREQUENCY_MISMATCH_ELEMENTS = "frequencyMismatchElements"

  override def check(deviceId: String, payload: KafkaInboundMsg): Unit = {
    val tagsWithMissedFrequency = tagsWithFrequencyMismatch(deviceId, payload.tagList.toList)
    if (tagsWithMissedFrequency.nonEmpty) {
      publishAsTelemetry(payload.deviceName, tagsWithMissedFrequency)
      publishAsEvents(payload.deviceName, tagsWithMissedFrequency)
    }
  }

  private def publishAsEvents(deviceName: String, tagsWithMissedFrequency: List[String]): Unit = {
    try {
      val client = new MqttClient(mqttConnector.getMqttUrl, MqttClient.generateClientId, new MemoryPersistence)
      val options = new MqttConnectOptions
      options.setUserName(mqttConnector.getAccessToken)
      client.setTimeToWait(1000)
      client.connect(options)
      val event = DeviceEvent("data-quality-service", FREQUENCY_MISMATCH_ELEMENTS, JsonUtil.toJson(tagsWithMissedFrequency))
      client.publish(DEVICE_EVENT_TOPIC, JsonUtil.toJson(Map(deviceName -> event)).getBytes(), 0, false)
      client.close()
    } catch {
      case e: Exception => log.info("Exception " + e)
    }
  }

  private def publishAsTelemetry(deviceName: String, tagsWithMissedFrequency: List[String]): Unit = {
    mqttConnector.publish(JsonUtil.toJson(Map(FREQUENCY_MISMATCH_ELEMENTS -> tagsWithMissedFrequency)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  private def tagsWithFrequencyMismatch(deviceId: String, tagList: List[TsKvData]): List[String] = {
    val tagsMetadata = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata
      case Left(errorMsg: String) => log.error(s"""Missing metadata for $deviceId because "$errorMsg". Tag frequency quality check failed"""); return List.empty
    }
    tagsMetadata.filter(metadata => isFrequencyMismatch(metadata, tagList)).map(_.tag)
  }

  private def isFrequencyMismatch(metadata: TagMetaData, tagList: List[TsKvData]): Boolean = {
    val tagPresenceCount = tagList.map(_.tag).count(metadata.tag.equals(_))
    val timestampRange = tagList.map(_.ts)
    val timeWindow = timestampRange.max - timestampRange.min + 1
    val expectedCount = (timeWindow / metadata.avgTagFrequency.toLong).round

    if (metadata.avgTagFrequency.toLong > timeWindow) {
      log.error(s"Device frequency (${metadata.avgTagFrequency}) cannot be higher than the configured timewindow ($timeWindow)")
      return true
    }

    if (tagPresenceCount == expectedCount) {
      false
    } else if (tagPresenceCount <= expectedCount) {
      (((expectedCount.toFloat - tagPresenceCount.toFloat) / expectedCount.toFloat) * 100) > missmatchThreshold
    } else {
      (((tagPresenceCount.toFloat - expectedCount.toFloat) / tagPresenceCount.toFloat) * 100) > missmatchThreshold
    }
  }
}
