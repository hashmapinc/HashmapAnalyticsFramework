package com.hashmap.dataquality.actor

import akka.actor.Actor
import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.ToActorMsg
import com.hashmap.dataquality.metadata.{MetadataDao, TagMetaData}
import com.hashmap.dataquality.qualitycheck.QualityCheckingService
import com.hashmap.dataquality.util.JsonUtil
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory

class DeviceActor(actorSystemContext: ActorSystemContext) extends Actor {

  private val MQTT_ATTRIBUTE_TOPIC = "v1/devices/me/attributes"

  private val log = LoggerFactory.getLogger(classOf[MetadataDao])
  private var subscriptionState = false
  private var deviceToken = ""

  override def receive: PartialFunction[Any, Unit] = {
      case msg: ToActorMsg =>
        processToActorMsg(msg)
      case _ =>
  }


  def processToActorMsg(msg: ToActorMsg): Unit ={
    if (!subscriptionState) {
      deviceToken = fetchDeviceToken(msg.deviceId)
      if(!deviceToken.contentEquals("")) {
        createAttributeSubscription(msg.deviceId)
      }
    }
    ApplicationContextProvider.getApplicationContext.getBean(classOf[QualityCheckingService])
      .processForQualityChecks(msg.deviceId, msg.kafkaInboundMsg);
  }

  private def createAttributeSubscription(deviceId: String): Unit = {

    val persistence = new MemoryPersistence
    val client = new MqttClient(actorSystemContext.MQTT_URL, MqttClient.generateClientId, persistence)

    val options = new MqttConnectOptions()
    options.setUserName(deviceToken)
    client.connect(options)
    client.subscribe(MQTT_ATTRIBUTE_TOPIC)

    val callback = new MqttCallback {

      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        println("Receiving Data, Topic : %s, Message : %s".format(topic, message))
        val sharedAttrib: String = new String(message.getPayload)
        try {
          val sharedAttribMap: Map[String, List[TagMetaData]] = JsonUtil.fromJson[Map[String, List[TagMetaData]]](sharedAttrib)
          sharedAttribMap foreach (x => if (x._1.contentEquals("mandatory_tags")) {
            actorSystemContext.metadataFetchService.saveMetaDataForDevice(deviceId, x._2)
            subscriptionState = true
          })

        } catch {
          case e: Exception => log.info("Exception is {}", e)
        }
      }

      override def connectionLost(cause: Throwable): Unit = {
        log.info("Connection lost {}", cause)
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {

      }
    }

    client.setCallback(callback)

  }

  def fetchDeviceToken(deviceId: String): String = actorSystemContext.metadataFetchService.getMetadataFromRemote(deviceId) match {
    case Right(token) => token
    case Left(error) => log.info("Error occurred in fetching {}", error); ""
  }

}
