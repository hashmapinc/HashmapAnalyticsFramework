package com.hashmap.dataquality.streams

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.hashmap.dataquality.data.{KafkaInboundMsg, ToActorMsg}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}

import scala.collection.parallel.immutable
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer

import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink}
import com.hashmap.dataquality.data.{KafkaInboundMsg, ToActorMsg}
import com.hashmap.dataquality.serdes.TelemetryDataSerde
import com.hashmap.dataquality.service.ActorService
import com.hashmap.dataquality.streams.{KafkaConsumerStream, StreamsService}
import com.typesafe.config.Config
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

import scala.collection.immutable
import scala.concurrent.duration._


trait DataQualityService[T] extends hasSource[T] with hasFlow with hasSink {
  implicit val system: ActorSystem = ActorSystem("data-quality")
  implicit val materializer: Materializer = ActorMaterializer()

  def runCheck(): Unit ={
    getSource().via(getFlow()).runWith(getSink())
  }

}

trait hasSource[T] {
  protected def getSource(): Source[(String, KafkaInboundMsg), T]
}

trait hasFlow {
  protected def getFlow() = {
    Flow[(String, KafkaInboundMsg)].groupedWithin(1000, 60 second).map(entry => entry.seq.map(x => new ToActorMsg(x._1, x._2)))
  }
}

trait hasSink {
  @Autowired
  private val actorService: ActorService = null

  protected def getSink() ={
    Sink.actorRef[Seq[ToActorMsg]](actorService.getActorContext.masterActor, onCompleteMessage = "stream completed")
  }
}

class KafkaBasedDataQualitService {
  this: DataQualityService[Consumer.Control] =>
  override def getSource(): Source[(String, KafkaInboundMsg), Consumer.Control] = {
    val config: Config = system.settings.config.getConfig("akka.kafka.consumer")
    val bootstrapServers = "kafka:9092"

    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, (new TelemetryDataSerde).deserializer())
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")

    val plainSource: Source[ConsumerRecord[String, KafkaInboundMsg], Consumer.Control] =
      Consumer.plainSource(consumerSettings, Subscriptions.topics("dq-topic"))

    val source: Source[(String, KafkaInboundMsg), Consumer.Control] = plainSource.map(entry => (entry.key(), entry.value()))
    source
  }
}

class Sample {
  val kdqs = new KafkaBasedDataQualitService with DataQualityService[Consumer.Control]
  kdqs.runCheck()
}