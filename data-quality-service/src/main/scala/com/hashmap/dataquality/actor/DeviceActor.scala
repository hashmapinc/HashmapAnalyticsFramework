package com.hashmap.dataquality.actor

import akka.actor.Actor
import com.hashmap.dataquality.data.Msgs.ToActorMsg
import com.hashmap.dataquality.metadata.DataQualityMetaData
import com.hashmap.dataquality.util.JsonUtil
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import scala.util.control.Exception._

class DeviceActor(actorSystemContext: ActorSystemContext) extends Actor {

  private val MQTT_ATTRIBUTE_TOPIC = "v1/devices/me/attributes"

  private val log = LoggerFactory.getLogger(classOf[DeviceActor])
  private var subscriptionState = false

  private val MQTT_URL_FORMAT = "tcp://%s:%s"

  override def receive: PartialFunction[Any, Unit] = {
      case msg: ToActorMsg =>
        processToActorMsg(msg)
      case _ =>
  }


  def processToActorMsg(msg: ToActorMsg): Unit ={
    if (!subscriptionState) {
      val deviceMetaData = fetchDeviceMetadata(msg.deviceId)
      createAttributeSubscription(msg.deviceId, deviceMetaData.token)
      if(deviceMetaData != null && deviceMetaData.metaData != null)
        actorSystemContext.metadataService.saveMetaDataForDevice(msg.deviceId, deviceMetaData.metaData)
    }
    actorSystemContext.qualityCheckingService.processForQualityChecks(msg.deviceId, msg.inboundMsg)
  }

  private def createAttributeSubscription(deviceId: String, deviceToken: String): Unit = {

    val persistence = new MemoryPersistence
    val mqttUrl = String.format(MQTT_URL_FORMAT, actorSystemContext.MQTT_BIND_ADDRESS, actorSystemContext.MQTT_BIND_PORT)
    val client: MqttClient = new MqttClient(mqttUrl, MqttClient.generateClientId, persistence)

    makeConnection(client, deviceToken) match {
      case Right(value) => subscribe(client, deviceId)
      case Left(error) => log.error(s"""Connection exception for device "$deviceId" {}""", error)
    }
  }

  private def makeConnection(client: MqttClient, deviceToken: String): Either[Throwable, Unit] = {
    val options = new MqttConnectOptions()
    options.setUserName(deviceToken)
    allCatch.either(client.connect(options))
  }

  private def subscribe(client: MqttClient, deviceId: String): Unit = {
    client.subscribe(MQTT_ATTRIBUTE_TOPIC)
    val callback = new MqttCallback {

      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        log.info("Receiving Data, Topic : %s, Message : %s".format(topic, message))
        val sharedAttribute: String = new String(message.getPayload)
        parseJson(sharedAttribute) match {
          case Right(value) => value.map(entry => if(entry._1.contentEquals("quality_meta_data")){
            actorSystemContext.metadataService.saveMetaDataForDevice(deviceId, entry._2)
          })
          case Left(error) => log.error(s"""Exception occurred while receiving subscription for device "$deviceId". Exception {}""", error)
        }
      }

      override def connectionLost(cause: Throwable): Unit = {
        log.info("Connection lost {}", cause)
        subscriptionState = false
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
        log.info(s"""Delivery complete for deviceId "$deviceId" """)
      }

    }
    client.setCallback(callback)
    subscriptionState = true
  }

  private def parseJson(json: String): Either[Throwable, Map[String, String]] = {
    allCatch.either(JsonUtil.fromJson[Map[String, String]](json))
  }

  private def fetchDeviceMetadata(deviceId: String): DataQualityMetaData = actorSystemContext.metadataService.getMetadataFromRemote(deviceId) match {
    case Right(deviceMetaData) => deviceMetaData
    case Left(error) => log.error(s"""Error occurred in fetching device metaData for deviceId "$deviceId". Error {}""", error); new DataQualityMetaData
  }

}
