package com.hashmap.dataquality.actor

import akka.actor.Actor
import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.ToActorMsg
import com.hashmap.dataquality.metadata.{DataQualityMetaData, MetadataDao}
import com.hashmap.dataquality.qualitycheck.QualityCheckingService
import com.hashmap.dataquality.util.JsonUtil
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory

class DeviceActor(actorSystemContext: ActorSystemContext) extends Actor {

  private val MQTT_ATTRIBUTE_TOPIC = "v1/devices/me/attributes"

  private val log = LoggerFactory.getLogger(classOf[MetadataDao])
  private var subscriptionState = false

  override def receive: PartialFunction[Any, Unit] = {
      case msg: ToActorMsg =>
        processToActorMsg(msg)
      case _ =>
  }


  def processToActorMsg(msg: ToActorMsg): Unit ={
    if (!subscriptionState) {
      val deviceMetaData = fetchDeviceMetadata(msg.deviceId)
      if(deviceMetaData.metaData != null)
        actorSystemContext.metadataService.saveMetaDataForDevice(msg.deviceId, deviceMetaData.metaData)
      if(deviceMetaData.token != null) {
        createAttributeSubscription(msg.deviceId, deviceMetaData.token)
      }
    }
    ApplicationContextProvider.getApplicationContext.getBean(classOf[QualityCheckingService])
      .processForQualityChecks(msg.deviceId, msg.kafkaInboundMsg)
  }

  private def createAttributeSubscription(deviceId: String, deviceToken: String): Unit = {

    val persistence = new MemoryPersistence
    val client = new MqttClient(actorSystemContext.MQTT_URL, MqttClient.generateClientId, persistence)

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
          sharedAttributeMap foreach (x => if (x._1.contentEquals("mandatory_tags")) {
            actorSystemContext.metadataService.saveMetaDataForDevice(deviceId, x._2)
          })

        } catch {
          case e: Exception => log.info("Exception is {}", e)
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
  }

  def fetchDeviceMetadata(deviceId: String): DataQualityMetaData = actorSystemContext.metadataService.getMetadataFromRemote(deviceId) match {
    case Right(deviceMetaData) => deviceMetaData
    case Left(error) => log.info("Error occurred in fetching {}", error); new DataQualityMetaData
  }

}
