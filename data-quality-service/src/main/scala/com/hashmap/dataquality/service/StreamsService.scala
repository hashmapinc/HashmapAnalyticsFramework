package com.hashmap.dataquality.service

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.hashmap.dataquality.streams.{FlowCreator, SinkCreator, SourceCreator}

trait StreamsService[T] extends SourceCreator [T] with SinkCreator with FlowCreator {

  implicit val system: ActorSystem = actorService.getActorContext.actorSystem
  implicit val materializer: Materializer = ActorMaterializer()

  def runGraph(): Unit = {
    createSource().via(createFlow()).to(createSink()).run()
  }

}
