package com.hashmap.haf.scheduler.pubsub.impl

import java.util
import java.util.{Collections, Properties}

import akka.actor.ActorRef
import com.hashmap.haf.scheduler.pubsub.api.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.concurrent.Future
import scala.util.Try

object KafkaConsumer extends Consumer{
  val props = readKafkaConfigs
  val consumer = new KafkaConsumer[String, String](props)
  var isNewSubscriberAdded = false

  private def readKafkaConfigs = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("group.id", "test-consumer-group")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props
  }

  private def listen = {
    while(isNewSubscriberAdded && !consumer.subscription().isEmpty){
      val records = consumer.poll(100)

    }
  }


  override def subscribe(topic: String, listenerActor: ActorRef, msg: Any): Unit =
    consumer.subscribe(Collections.singletonList(topic))
    isNewSubscriberAdded = true


  override def unsubscribe(topics: String*): Future[Try[Boolean]] = ???
}
