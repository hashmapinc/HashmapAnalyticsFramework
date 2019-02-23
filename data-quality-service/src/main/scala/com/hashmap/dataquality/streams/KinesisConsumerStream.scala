package com.hashmap.dataquality.streams

import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.stream.alpakka.kinesis.ShardSettings
import akka.stream.alpakka.kinesis.scaladsl.KinesisSource
import akka.stream.scaladsl.Source
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.kinesis.model.{Record, ShardIteratorType}
import com.amazonaws.services.kinesis.{AmazonKinesisAsync, AmazonKinesisAsyncClientBuilder}
import com.hashmap.dataquality.data.{InboundMsg, KinesisInboundMsg}
import com.hashmap.dataquality.util.JsonUtil

import scala.concurrent.duration._

class KinesisConsumerStream extends StreamsService [NotUsed]{

  val awsCreds = new BasicAWSCredentials(appConfig.ACCESS_KEY, appConfig.SECRET_KEY)

  override def createSource(): Source[(String, InboundMsg), NotUsed] = {
    getKinesisSource
      .map(record => new String(record.getData.array(), StandardCharsets.UTF_8))
      .map(entry => JsonUtil.fromJson[KinesisInboundMsg](entry))
      .map(entry => (entry.deviceId, InboundMsg(entry.deviceName, entry.tagList)))
  }

  private def getKinesisSource: Source[Record, NotUsed] = {
    implicit val amazonKinesisAsync: AmazonKinesisAsync =
      AmazonKinesisAsyncClientBuilder.standard
        .withRegion(Regions.US_EAST_2)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build

    system.registerOnTermination(amazonKinesisAsync.shutdown())
    val settings = ShardSettings(streamName = appConfig.STREAM_NAME,
      shardId = appConfig.SHARD_ID,
      shardIteratorType = ShardIteratorType.LATEST,
      refreshInterval = 1 second,
      limit = 500)

    KinesisSource.basic(settings, amazonKinesisAsync)

  }
}
