package com.hashmap.haf.scheduler.api

import akka.actor.ActorRef

trait Scheduler {
  def createJob(name: String, cronExpression: String): Boolean
  def submitJob(name: String, subscriberActor: ActorRef, msg: AnyRef): Boolean
  def updateJob(_name: String, _subscriberActor: ActorRef, _cronExpression: String, msg: AnyRef): Boolean
  def suspendJob(name: String): Boolean
  def resumeJob(name: String): Boolean
  def SuspendAll: Boolean
}