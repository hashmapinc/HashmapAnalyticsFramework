package com.hashmap.dataquality

import java.util.Optional

import com.hashmap.dataquality.data.Msgs.DeviceEvent
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory

import scala.util.control.Exception._

class MqttPublisher

object MqttPublisher {

  private val DEVICE_EVENT_TOPIC = "v1/gateway/events"
  private val log = LoggerFactory.getLogger(classOf[MqttPublisher])
  private var aMqttClient: MqttAsyncClient = null

  def publishAsTelemetry(deviceName: String, missingTags: List[String], mqttConnector: MqttConnector, notification: String): Unit ={
    mqttConnector.publish(JsonUtil.toJson(Map(notification -> missingTags)),
      Optional.of(System.currentTimeMillis()),
      Optional.empty(), deviceName)
  }

  def publishAsEvents(deviceName: String, missingTags: List[String], mqttConnector: MqttConnector, notification: String): Unit = {
    getConnection(mqttConnector) match {
      case Right(client) => publish(client, notification, missingTags, deviceName) match {
        case Left(error) => log.info(s"""Exception {}""", error)
        case Right(unit) => unit
      }
      case Left(error) => log.info(s"""Connection exception $deviceName {}""", error)
    }
  }

  private def getConnection(mqttConnector: MqttConnector): Either[Throwable, MqttAsyncClient] = {
    allCatch.either({
      if(aMqttClient == null) {
        aMqttClient = new MqttAsyncClient(mqttConnector.getMqttUrl, MqttClient.generateClientId, new MemoryPersistence)
      }
      if(!aMqttClient.isConnected) {
        val options = new MqttConnectOptions
        options.setUserName(mqttConnector.getAccessToken)
        options.setAutomaticReconnect(true)
        options.setCleanSession(false)
        aMqttClient.connect(options, null, getConnectionActionCallback())
      }
      aMqttClient
    })
  }

  private def publish(client: IMqttAsyncClient, notification: String, missingTags: List[String], deviceName: String): Either[Throwable, Unit] = {
    val event = DeviceEvent("data-quality-service", notification, JsonUtil.toJson(missingTags))

    allCatch.either({
      if (client.isConnected) {
        val bytesToSend: Array[Byte] = JsonUtil.toJson(Map(deviceName -> event)).getBytes()
        client.publish(DEVICE_EVENT_TOPIC, bytesToSend, 1, false,
          null, getPublishActionCallback())
      } else
        log.info("Client Not connected ")
    })
  }

  private def getPublishActionCallback (): IMqttActionListener = {
    new IMqttActionListener {
      override def onSuccess(asyncActionToken: IMqttToken): Unit = {
        log.info("Event published successfully ")
      }

      override def onFailure(asyncActionToken: IMqttToken, exception: Throwable): Unit = {
        log.info(s"""On Publish failure $exception """)
      }
    }
  }

  private def getConnectionActionCallback(): IMqttActionListener = {
    new IMqttActionListener {
      override def onSuccess(asyncActionToken: IMqttToken): Unit = {
        log.info("Connection successful")
      }

      override def onFailure(asyncActionToken: IMqttToken, exception: Throwable): Unit = {
        log.info(s"""Connection failed $exception""")
      }
    }
  }

}
