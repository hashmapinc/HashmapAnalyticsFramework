package com.hashmap.dataquality.service

import akka.actor.{ActorRef, ActorSystem, Props}
import com.hashmap.dataquality.actor.{ActorSystemContext, MasterActor}
import com.hashmap.dataquality.data.Msgs.{InboundMsg, ToActorMsg}
import javax.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ActorService {

  private val ACTOR_SYSTEM_NAME = "AkkaSystem"
  private val MASTER_ACTOR_NAME = "MasterActor"
  private val MASTER_ACTOR_DISPATCHER_NAME = "master-dispatcher"

  @Autowired
  private val actorContext: ActorSystemContext = null

  // Getter
  def getActorContext: ActorSystemContext = actorContext


  @PostConstruct
  def init(): Unit = {
    val actorSystem: ActorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME, actorContext.config)
    actorContext.actorSystem = actorSystem

    val masterActor = actorSystem.actorOf(Props(new MasterActor(actorContext)).withDispatcher(MASTER_ACTOR_DISPATCHER_NAME), MASTER_ACTOR_NAME)
    actorContext.masterActor = masterActor
  }

  def process(key: String, msg: InboundMsg): Unit = {
    actorContext.masterActor.tell(msg = ToActorMsg(key, msg), sender = ActorRef.noSender)
  }

}
