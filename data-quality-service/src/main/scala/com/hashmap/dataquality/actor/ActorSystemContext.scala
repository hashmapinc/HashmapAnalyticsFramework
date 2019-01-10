package com.hashmap.dataquality.actor

import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.dataquality.metadata.MetadataFetchService
import com.typesafe.config.{Config, ConfigFactory}
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

@Component
class ActorSystemContext {

  private val AKKA_CONF_FILE_NAME = "actor-system.conf"

  var masterActor: ActorRef = _
  var actorSystem: ActorSystem = _
  var config: Config = _

  @Autowired
  val metadataFetchService: MetadataFetchService = null

  @Value("${tempus.mqtt-url}") val MQTT_URL: String = ""

  @PostConstruct
  def init() = {
    this.config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load)
  }

}
