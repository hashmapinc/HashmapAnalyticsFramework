package com.hashmap.dataquality.actor

import akka.actor.{Actor, ActorRef, Props}
import com.hashmap.dataquality.data.ToActorMsg

import scala.collection.mutable

class MasterActor(actorSystemContext: ActorSystemContext) extends Actor {

  private val deviceActorMap: mutable.Map[String, ActorRef] = new mutable.HashMap[String, ActorRef]()

  private val DEVICE_ACTOR_DISPATCHER_NAME = "core-dispatcher"

  override def receive: PartialFunction[Any, Unit] = {
      case inboundMsg: ToActorMsg =>
        processToMasterActorMsg(inboundMsg)
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
