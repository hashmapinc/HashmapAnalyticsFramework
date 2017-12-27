package com.hashmap.haf.scheduler.pubsub.api

import scala.concurrent.Future
import scala.util.Try

trait PubSubClient[A, B] {
  def publish(topic: String, message: A): Future[Try[Boolean]]
  def subscribe(topics: String*)(callback: A => B): Unit
  def unsubscribe(topics: String*): Future[Try[Boolean]]
}
