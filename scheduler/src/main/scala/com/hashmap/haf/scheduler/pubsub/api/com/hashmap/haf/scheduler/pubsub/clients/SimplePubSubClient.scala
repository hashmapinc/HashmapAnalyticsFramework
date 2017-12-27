package com.hashmap.haf.scheduler.pubsub.api.com.hashmap.haf.scheduler.pubsub.clients


import com.hashmap.haf.scheduler.pubsub.api.PubSubClient

import scala.concurrent.Future
import scala.util.Try

class SimplePubSubClient extends PubSubClient[String, Boolean]{

  import scala.concurrent.ExecutionContext.Implicits.global


  val store: Map[String, String] = Map.empty

  override def publish(topic: String, message: String) = Future {
    Try {
      store + (topic -> message)
      true
    }
  }

  override def subscribe(topics: String*)(callback: String => Boolean) = topics.foreach(callback(_))

  override def unsubscribe(topics: String*) = Future {
    Try {
      store
      false
    }
  }
}
