package com.hashmap.dataquality.actor

import akka.actor.{ActorRef, Props, UntypedActor}
import com.hashmap.dataquality.data.TsKvEntry
import com.hashmap.dataquality.message.KafkaInboundMsg
import com.hashmap.dataquality.service.ActorService
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MasterActor extends UntypedActor {

  @Autowired
  private var actorService: ActorService = _

  private var deviceActorMap: mutable.Map[String, ActorRef] = _

  override def onReceive(message: Any): Unit = {
    message match {
      case inboundMsg: KafkaInboundMsg =>
        processKafkaInboundMsg(inboundMsg)
      case _ =>
    }
  }

  def processKafkaInboundMsg(msg: KafkaInboundMsg): Unit = {
    msg.msg.foreach{case (key, value) => getOrCreateDeviceActor(key, value)}
  }

  def getOrCreateDeviceActor(key: String, kvEntryList: ListBuffer[TsKvEntry]): Unit = {
    if(deviceActorMap.contains(key))
      deviceActorMap(key).tell(kvEntryList, context.self)
    else {
      val deviceActor = actorService.getActorContext.actorSystem.actorOf(Props[DeviceActor], key)
      deviceActorMap.put(key, deviceActor)
      deviceActor.tell(kvEntryList, context.self)
    }
  }
}
