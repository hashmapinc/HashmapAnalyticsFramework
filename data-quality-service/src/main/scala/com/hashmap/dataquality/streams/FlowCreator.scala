package com.hashmap.dataquality.streams

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.hashmap.dataquality.ApplicationContextProvider
import com.hashmap.dataquality.config.AppConfig
import com.hashmap.dataquality.data.{InboundMsg, ToActorMsg}

import scala.collection.immutable
import scala.concurrent.duration._

trait FlowCreator {

  protected val appConfig: AppConfig = ApplicationContextProvider.getApplicationContext.getBean(classOf[AppConfig])

  protected def createFlow(): Flow[(String, InboundMsg), ToActorMsg, NotUsed] = {
    Flow[(String, InboundMsg)].groupedWithin(Integer.MAX_VALUE, appConfig.TIME_WINDOW second).mapConcat(entry => {
      val ret: immutable.Iterable[ToActorMsg] = reduceByKey(entry).map(e => ToActorMsg(e._1, e._2))
      ret
    })
  }

  private def reduceByKey(entry: immutable.Seq[(String, InboundMsg)]) = {
    entry.groupBy(_._1).mapValues(e => e.map(_._2).reduce((msg1, msg2) => {
      msg1.tagList ++= msg2.tagList
      msg1
    }))
  }
}
