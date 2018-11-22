package com.hashmap.dataquality.service

import akka.actor.{ActorRef, ActorSystem, Props}
import com.hashmap.dataquality.actor.{ActorSystemContext, MasterActor}
import com.hashmap.dataquality.message.KafkaInboundMsg
import javax.annotation.PostConstruct
import lombok.Data
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ActorService {

  private val ACTOR_SYSTEM_NAME = "AkkaSystem"
  private val MASTER_ACTOR_NAME = "MasterActor"

  @Autowired
  private var actorContext: ActorSystemContext = _

  // Getter
  def getActorContext = actorContext


  @PostConstruct
  def init() = {
    val actorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME, actorContext.config)
    actorContext.actorSystem = actorSystem

    val appActor = actorSystem.actorOf(Props[MasterActor], MASTER_ACTOR_NAME)
    actorContext.actorApp = appActor

    print("Intialized!!!")
  }

  def process(msg: KafkaInboundMsg): Unit = {
    actorContext.actorApp.tell(msg, ActorRef.noSender)
  }

}
