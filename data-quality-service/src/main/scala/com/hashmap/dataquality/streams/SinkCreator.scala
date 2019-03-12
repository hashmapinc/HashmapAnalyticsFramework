package com.hashmap.dataquality.streams

import akka.NotUsed
import akka.stream.scaladsl.Sink
import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.Msgs.ToActorMsg
import com.hashmap.dataquality.service.ActorService

trait SinkCreator {
  protected val actorService: ActorService = ApplicationContextProvider.getApplicationContext.getBean(classOf[ActorService])

  def createSink(): Sink[ToActorMsg, NotUsed] = {
    Sink.actorRef[ToActorMsg](actorService.getActorContext.masterActor, onCompleteMessage = "stream completed")
  }
}
