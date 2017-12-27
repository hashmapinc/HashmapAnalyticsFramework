package com.hashmap.haf.scheduler.pubsub.api.com.hashmap.haf.scheduler.pubsub.clients

import com.hashmap.haf.scheduler.pubsub.api.PubSubClient

class RedisPubSubClient(val host: String, val port: Int) extends PubSubClient[String, Boolean]{

  override def publish(topic: String, message: String) = ???

  override def subscribe(topics: String*)(callback: String => Boolean) = ???

  override def unsubscribe(topics: String*) = ???
}
