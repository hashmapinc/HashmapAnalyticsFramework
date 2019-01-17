package com.hashmap.dataquality

import java.util.Optional

import com.hashmap.dataquality.data.DeviceEvent
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions}
import org.slf4j.LoggerFactory

class MqttPublisher

object MqttPublisher {

  private val DEVICE_EVENT_TOPIC = "v1/gateway/events"
  private val log = LoggerFactory.getLogger(classOf[MqttPublisher])

  def publishAsTelemetry(deviceName: String, missingTags: List[String], mqttConnector: MqttConnector, notification: String): Unit ={
    mqttConnector.publish(JsonUtil.toJson(Map(notification -> missingTags)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  def publishAsEvents(deviceName: String, missingTags: List[String], mqttConnector: MqttConnector, notification: String): Unit = {
    try {
      val client = new MqttClient(mqttConnector.getMqttUrl, MqttClient.generateClientId, new MemoryPersistence)
      val options = new MqttConnectOptions
      options.setUserName(mqttConnector.getAccessToken)
      client.setTimeToWait(1000)
      client.connect(options)
      val event = DeviceEvent("data-quality-service", notification, JsonUtil.toJson(missingTags))
      client.publish(DEVICE_EVENT_TOPIC, JsonUtil.toJson(Map(deviceName -> event)).getBytes(), 0, false)
      client.close()
    } catch {
      case e: Exception => log.info("Exception " + e)
    }
  }
}