package com.hashmap.dataquality.actor

import akka.actor.UntypedActor
import com.hashmap.dataquality.message.DeviceTelemetryMsg

class DeviceActor extends UntypedActor{
  override def onReceive(message: Any): Unit = {
    message match {
      case telemetryMsg: DeviceTelemetryMsg =>
        processDeviceTelemetryMsg(telemetryMsg)
      case _ =>
    }
  }

  def processDeviceTelemetryMsg(msg: DeviceTelemetryMsg): Unit ={
    msg.deviceTelemetryMsg.result()
  }
}
