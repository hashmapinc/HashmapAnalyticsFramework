package com.hashmap.dataquality.streams

import akka.stream.scaladsl.Sink
import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.data.ToActorMsg
import com.hashmap.dataquality.service.ActorService

import scala.collection.immutable

trait SinkCreator {
  protected val actorService: ActorService = ApplicationContextProvider.getApplicationContext.getBean(classOf[ActorService])

  def createSink() ={
    Sink.actorRef[immutable.Seq[ToActorMsg]](actorService.getActorContext.masterActor, onCompleteMessage = "stream completed")
  }
}
