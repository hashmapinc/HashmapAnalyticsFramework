package com.hashmap.dataquality.actor

import akka.actor.Actor
import com.hashmap.dataquality.data.ToActorMsg
import com.hashmap.dataquality.metadata.DataQualityMetaData
import com.hashmap.dataquality.util.JsonUtil
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory

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
    val client = new MqttClient(mqttUrl, MqttClient.generateClientId, persistence)

    try {
      val options = new MqttConnectOptions()
      options.setUserName(deviceToken)
      client.connect(options)
      client.subscribe(MQTT_ATTRIBUTE_TOPIC)

      val callback = new MqttCallback {
        override def messageArrived(topic: String, message: MqttMessage): Unit = {
          log.info("Receiving Data, Topic : %s, Message : %s".format(topic, message))
          val sharedAttribute: String = new String(message.getPayload)
          try {
            val sharedAttributeMap: Map[String, String] = JsonUtil.fromJson[Map[String, String]](sharedAttribute)
            sharedAttributeMap foreach (x => if (x._1.contentEquals("quality_meta_data")) {
              actorSystemContext.metadataService.saveMetaDataForDevice(deviceId, x._2)
            })

          } catch {
            case e: Exception => log.error(s"""Exception occurred while receiving subscription for device "$deviceId". Exception {}""", e)
          }
        }

        override def connectionLost(cause: Throwable): Unit = {
          log.info("Connection lost {}", cause)
          subscriptionState = false
        }

        override def deliveryComplete(token: IMqttDeliveryToken): Unit = {

        }
      }
      client.setCallback(callback)

      subscriptionState = true
    } catch {
      case e : Exception => log.error(s"""Connection exception for device "$deviceId" {}""", e)
    }
  }

  private def fetchDeviceMetadata(deviceId: String): DataQualityMetaData = actorSystemContext.metadataService.getMetadataFromRemote(deviceId) match {
    case Right(deviceMetaData) => deviceMetaData
    case Left(error) => log.error(s"""Error occurred in fetching device metaData for deviceId "$deviceId". Error {}""", error); new DataQualityMetaData(null, null)
  }

}
