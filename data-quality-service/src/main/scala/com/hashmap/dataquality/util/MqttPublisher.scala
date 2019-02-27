package com.hashmap.dataquality

import java.util.Optional

import com.hashmap.dataquality.data.Msgs.DeviceEvent

import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions}
import org.slf4j.LoggerFactory
import scala.util.control.Exception._

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
    makeConnection(mqttConnector) match {
      case Right(client) => publish(client, notification, missingTags, deviceName) match {
        case Left(error) => log.info(s"""Exception {}""", error)
      }
      case Left(error) => log.info(s"""Connection exception $deviceName {}""", error)
    }
  }

  private def makeConnection(mqttConnector: MqttConnector): Either[Throwable, MqttClient] = {
    allCatch.either({
      val client = new MqttClient(mqttConnector.getMqttUrl, MqttClient.generateClientId, new MemoryPersistence)
      val options = new MqttConnectOptions
      options.setUserName(mqttConnector.getAccessToken)
      client.setTimeToWait(1000)
      client.connect(options)
      client
    })
  }

  private def publish(client: MqttClient, notification: String, missingTags: List[String], deviceName: String): Either[Throwable, Unit] = {
    val event = DeviceEvent("data-quality-service", notification, JsonUtil.toJson(missingTags))
    allCatch.either({
      client.publish(DEVICE_EVENT_TOPIC, JsonUtil.toJson(Map(deviceName -> event)).getBytes(), 0, false)
      client.close()
    })
  }
}