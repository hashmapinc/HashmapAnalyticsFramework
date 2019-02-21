package com.hashmap.dataquality.actor

import akka.actor.{Actor, ActorRef, Props}
import com.hashmap.dataquality.data.ToActorMsg

import scala.collection.{immutable, mutable}

class MasterActor(actorSystemContext: ActorSystemContext) extends Actor {

  private val deviceActorMap: mutable.Map[String, ActorRef] = new mutable.HashMap[String, ActorRef]()

  private val DEVICE_ACTOR_DISPATCHER_NAME = "core-dispatcher"

  override def receive: PartialFunction[Any, Unit] = {
      case inboundMsg: ToActorMsg =>
        processToMasterActorMsg(inboundMsg)

      case inboundList: immutable.Seq[ToActorMsg] =>
        aggregateList(inboundList)
      case _ =>

  }

  def aggregateList(msgs: Seq[ToActorMsg]) = {
    val map: mutable.Map[String, ToActorMsg] = new mutable.HashMap[String, ToActorMsg]()
    msgs.foreach(msg => {
      if(!map.contains(msg.deviceId))
        map.put(msg.deviceId, msg)
      else {
        map.get(msg.deviceId).get.kafkaInboundMsg.tagList ++= msg.kafkaInboundMsg.tagList
      }
    })
    map.foreach(entry => processToMasterActorMsg(entry._2))
  }

  def processToMasterActorMsg(msg: ToActorMsg): Unit = {
    getOrCreateDeviceActor(msg)
  }

  def getOrCreateDeviceActor(msg: ToActorMsg): Unit = {
    if(deviceActorMap.contains(msg.deviceId))
      deviceActorMap(msg.deviceId).tell(msg, context.self)
    else {
      val deviceActor = context.actorOf(Props(new DeviceActor(actorSystemContext)).withDispatcher(DEVICE_ACTOR_DISPATCHER_NAME), msg.deviceId)
      deviceActorMap.put(msg.deviceId, deviceActor)
      deviceActor.tell(msg, context.self)
    }
  }

}
