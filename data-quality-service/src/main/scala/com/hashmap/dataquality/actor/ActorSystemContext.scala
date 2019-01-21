package com.hashmap.dataquality.actor

import akka.actor.{ActorRef, ActorSystem}
import com.hashmap.dataquality.metadata.MetadataService
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
  val metadataService: MetadataService = null

  @Value("${tempus.mqtt-bind-address}") val MQTT_BIND_ADDRESS: String = ""

  @Value("${tempus.mqtt-bind-port}") val MQTT_BIND_PORT: String = ""

  @Value("${tempus.gateway-access-token}") val ACCESS_TOKEN: String = ""

  @PostConstruct
  def init() = {
    this.config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load)
  }

}
