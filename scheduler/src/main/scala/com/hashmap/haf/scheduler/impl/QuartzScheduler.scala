package com.hashmap.haf.scheduler.impl

import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.haf.scheduler.api.Scheduler
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import scala.util.Try

case class QuartzScheduler(system: ActorSystem) extends Scheduler {
  private val scheduler = QuartzSchedulerExtension(system)

  override def createJob(_name: String, _cronExpression: String) = Try(scheduler.createSchedule(name = _name, cronExpression = _cronExpression)).isSuccess

  override def submitJob(_name: String, _subscriberActor: ActorRef, msg: AnyRef) = Try(scheduler.schedule(_name, _subscriberActor, msg)).isSuccess

  override def updateJob(_name: String, _subscriberActor: ActorRef, _cronExpression: String, msg: AnyRef) =
    Try(scheduler.rescheduleJob(_name, _subscriberActor, msg,  cronExpression = _cronExpression)).isSuccess

  override def suspendJob(_name: String) = Try(scheduler.suspendJob(_name)).isSuccess

  override def resumeJob(_name: String) = Try(scheduler.resumeJob(_name)).isSuccess

  override def SuspendAll = Try(scheduler.suspendAll()).isSuccess
}
