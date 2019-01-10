package com.hashmap.dataquality.actor

import akka.actor.{Actor, ActorRef, Props}
import com.hashmap.dataquality.data.ToActorMsg

import scala.collection.mutable

class MasterActor(actorSystemContext: ActorSystemContext) extends Actor {

  private var deviceActorMap: mutable.Map[String, ActorRef] = _

  private val DEVICE_ACTOR_DISPATCHER_NAME = "core-dispatcher"

  override def receive = {
      case inboundMsg: ToActorMsg =>
        processToMasterActorMsg(inboundMsg)

      case _ =>

  }

  def processToMasterActorMsg(msg: ToActorMsg): Unit = {
    getOrCreateDeviceActor(msg)
  }

  def getOrCreateDeviceActor(msg: ToActorMsg): Unit = {
    if(deviceActorMap.contains(msg.deviceId))
      deviceActorMap(msg.deviceId).tell(msg, context.self)
    else {
      val deviceActor = actorSystemContext.actorSystem.actorOf(Props(new DeviceActor(actorSystemContext)).withDispatcher(DEVICE_ACTOR_DISPATCHER_NAME), msg.deviceId)
      deviceActorMap.put(msg.deviceId, deviceActor)
      deviceActor.tell(msg, context.self)
    }
  }

}
