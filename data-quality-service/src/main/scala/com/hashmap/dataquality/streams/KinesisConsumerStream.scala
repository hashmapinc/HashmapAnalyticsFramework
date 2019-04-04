package com.hashmap.dataquality.streams

import java.nio.charset.StandardCharsets
import java.util

import akka.NotUsed
import akka.stream.alpakka.kinesis.ShardSettings
import akka.stream.alpakka.kinesis.scaladsl.KinesisSource
import akka.stream.scaladsl.Source
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.kinesis.model._
import com.amazonaws.services.kinesis.{AmazonKinesisAsync, AmazonKinesisAsyncClientBuilder}
import com.hashmap.dataquality.data.Msgs.InboundMsg
import com.hashmap.dataquality.service.StreamsService
import com.hashmap.dataquality.util.JsonUtil

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class KinesisConsumerStream extends StreamsService [NotUsed]{

  val awsCreds = new BasicAWSCredentials(appConfig.ACCESS_KEY, appConfig.SECRET_KEY)

  override def createSource(): Source[(String, InboundMsg), NotUsed] = {
    getKinesisSource
      .map(record => new String(record.getData.array(), StandardCharsets.UTF_8))
      .map(entry => JsonUtil.fromJson[InboundMsg](entry))
      .map(entry => (entry.deviceId.get, entry))
  }

  private def getShards(streamName: String, amazonKinesisAsync: AmazonKinesisAsync): util.List[Shard] = {
    val describeStreamRequest: DescribeStreamRequest = new DescribeStreamRequest
    describeStreamRequest.setStreamName(streamName)
    val shards: util.List[Shard] = new util.ArrayList[Shard]
    var exclusiveStartShardId: String = null
    do {
      describeStreamRequest.setExclusiveStartShardId(exclusiveStartShardId)
      val describeStreamResult: DescribeStreamResult = amazonKinesisAsync.describeStream(describeStreamRequest)
      shards.addAll(describeStreamResult.getStreamDescription.getShards)
      if (describeStreamResult.getStreamDescription.getHasMoreShards && shards.size > 0)
        exclusiveStartShardId = shards.get(shards.size - 1).getShardId
      else
        exclusiveStartShardId = null
    } while ( {
      exclusiveStartShardId != null
    })
    shards
  }

  private def getKinesisSource: Source[Record, NotUsed] = {
    implicit val amazonKinesisAsync: AmazonKinesisAsync =
      AmazonKinesisAsyncClientBuilder.standard
        .withRegion(Regions.US_EAST_2)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build
    
    val shards = getShards(appConfig.STREAM_NAME, amazonKinesisAsync)

    val shardSettingsList: List[ShardSettings] = shards.asScala.toList.map(shard => ShardSettings(appConfig.STREAM_NAME,
      shard.getShardId,
      shardIteratorType = ShardIteratorType.LATEST,
      refreshInterval = 1 second,
      limit = 500
    ))

    system.registerOnTermination(amazonKinesisAsync.shutdown())
    KinesisSource.basicMerge(shardSettingsList, amazonKinesisAsync)

  }
}
