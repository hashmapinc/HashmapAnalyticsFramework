package com.hashmap.haf.scheduler.pubsub.api

import akka.actor.ActorRef

import scala.concurrent.Future
import scala.util.Try

trait Consumer {
  def subscribe(topic: String, listenerActor: ActorRef, msg: Any): Unit
  def unsubscribe(topics: String*): Future[Try[Boolean]]
}
