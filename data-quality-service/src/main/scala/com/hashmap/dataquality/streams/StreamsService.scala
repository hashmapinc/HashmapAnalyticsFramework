package com.hashmap.dataquality.streams

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

trait StreamsService[T] extends SourceCreator [T] with SinkCreator with FlowCreator {

  implicit val system: ActorSystem = actorService.getActorContext.actorSystem
  implicit val materializer: Materializer = ActorMaterializer()

  def runGraph(): Unit = {
    createSource().via(createFlow()).to(createSink()).run()
  }

}
