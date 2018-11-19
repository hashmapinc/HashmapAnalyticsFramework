package com.hashmap.dataquality.actor

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}
import javax.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ActorSystemContext {

  private val AKKA_CONF_FILE_NAME = "actor-system.conf"

  var actorApp: ActorRef = _
  var actorSystem: ActorSystem = _
  var config: Config = _

  @PostConstruct
  def init() = {
    this.config = ConfigFactory.parseResources(AKKA_CONF_FILE_NAME).withFallback(ConfigFactory.load)
  }

}
