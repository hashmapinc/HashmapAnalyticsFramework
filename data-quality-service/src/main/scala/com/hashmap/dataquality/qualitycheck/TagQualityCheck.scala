package com.hashmap.dataquality.qualitycheck

import java.util.Optional

import com.google.gson.GsonBuilder
import com.hashmap.dataquality.data.{TelemetryData, TsKvData}
import com.hashmap.dataquality.metadata.MetadataFetchService
import com.hashmapinc.tempus.MqttConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TagQualityCheck extends QualityCheck {

  @Autowired
  private val metadataFetchService: MetadataFetchService = null

  private val MQTT_URL = "tcp://tempus.hashmapinc.com:1883"
  private val ACCESS_TOKEN = "DEVICE_GATEWAY_TOKEN"

  private val mqttConnector = new MqttConnector(MQTT_URL, ACCESS_TOKEN)

  override def check(deviceId: String, payload: TelemetryData): Unit = {
    val tagPresence: Map[String, Boolean] = checkTags(deviceId, payload.tagList.toList)

    publish(deviceId, tagPresence.filter(!_._2).keys.toList)
  }

  private def publish(deviceId: String, missingTags: List[String]): Unit = {
    mqttConnector.publish(new GsonBuilder().create().toJson(Map("missingElements" -> missingTags)), Optional.empty(), Optional.empty(), deviceId)
  }

  private def checkTags(deviceId: String, tagsPresent: List[TsKvData]): Map[String, Boolean] = {
    val qualityAttributes = metadataFetchService.getMetadataForDevice(deviceId) match {
      case Right(metadata) => metadata.attributes
      case Left(_) => return Map.empty //log
    }
    qualityAttributes.map(mandatoryAttributes => (mandatoryAttributes._1, tagsPresent.map(_.tag).contains(mandatoryAttributes._1)))
  }

}
