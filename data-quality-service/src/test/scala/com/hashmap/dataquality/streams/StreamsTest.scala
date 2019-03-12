package com.hashmap.dataquality.streams

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.hashmap.dataquality.data.Msgs.InboundMsg
import com.hashmap.dataquality.metadata.{DataQualityMetaData, MetadataService, TagMetaData}
import com.hashmap.dataquality.service.{StreamsApp, StreamsService}
import com.hashmap.dataquality.util.JsonUtil
import com.hashmapinc.tempus.MqttConnector
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{doNothing, never, times, when}
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito, MockitoAnnotations}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.{SpringBootContextLoader, SpringBootTest}
import org.springframework.context.annotation._
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration}


@ActiveProfiles(Array("streams-test"))
@WebAppConfiguration
@RunWith(classOf[SpringRunner])
@SpringBootTest
@ContextConfiguration(classes = Array(classOf[StreamsTest]), loader = classOf[SpringBootContextLoader])
@ComponentScan(Array("com.hashmap.dataquality"))
class StreamsTest {

  @Autowired
  val streamsApp: StreamsApp = null

  @Autowired
  val metadataService: MetadataService = null

  @Autowired
  val mqttConnector: MqttConnector = null

  var testSource : TestSource = _


  @Before
  def before(): Unit = {
    MockitoAnnotations.initMocks(this)
    doNothing().when(streamsApp).run()
    testSource = new TestSource
    Mockito.reset(mqttConnector, metadataService, streamsApp)
  }

  @Test
  def streamGraphFlowAllMissingTags(): Unit = {

    val metadataString = "[{\"tag\":\"waterTankLevel\", \"avgTagFrequency\": \"1000\"}, {\"tag\":\"Attn\", \"avgTagFrequency\": \"1000\"}, {\"tag\":\"TankLevel\", \"avgTagFrequency\": \"1000\"}]"
    val listTagMetaData = JsonUtil.fromJson[List[TagMetaData]](metadataString)
    val dataQualityMetaDataStr = "{\"token\":\"token\", \"metaData\": \"[{\\\"tag\\\":\\\"waterTankLevel\\\", \\\"avgTagFrequency\\\": \\\"1000\\\"}, {\\\"tag\\\":\\\"humidity\\\", \\\"avgTagFrequency\\\": \\\"1000\\\"}]\"}"
    val givenTagMetadata = JsonUtil.fromJson[DataQualityMetaData](dataQualityMetaDataStr)

    when(metadataService.getMetadataFromRemote(any())).
      thenReturn(Right(givenTagMetadata))

    when(metadataService.getMetadataForDevice(any())).
      thenReturn(Right(listTagMetaData))

    doNothing().when(metadataService).saveMetaDataForDevice(any(), any())

    testSource.runGraph()

    Thread.sleep(20000)
    val captor = ArgumentCaptor.forClass(classOf[String])

    Mockito.verify(mqttConnector, times(2)).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
    val res = captor.getAllValues

    assert(res.size() == 2)
    assert(res.get(0).toString == "{\"frequencyMismatchElements\":[\"waterTankLevel\",\"Attn\",\"TankLevel\"]}")
    assert(res.get(1).toString == "{\"missingElements\":[\"waterTankLevel\",\"Attn\",\"TankLevel\"]}")
  }

  @Test
  def streamGraphFlowSomeMissingTags(): Unit = {

    val metadataString = "[{\"tag\":\"waterTankLevel\", \"avgTagFrequency\": \"1000\"}, {\"tag\":\"humidity\", \"avgTagFrequency\": \"1000\"}]"
    val listTagMetaData = JsonUtil.fromJson[List[TagMetaData]](metadataString)
    val dataQualityMetaDataStr = "{\"token\":\"token\", \"metaData\": \"[{\\\"tag\\\":\\\"waterTankLevel\\\", \\\"avgTagFrequency\\\": \\\"1000\\\"}, {\\\"tag\\\":\\\"humidity\\\", \\\"avgTagFrequency\\\": \\\"1000\\\"}]\"}"
    val givenTagMetadata = JsonUtil.fromJson[DataQualityMetaData](dataQualityMetaDataStr)

    when(metadataService.getMetadataFromRemote(any())).
      thenReturn(Right(givenTagMetadata))

    when(metadataService.getMetadataForDevice(any())).
      thenReturn(Right(listTagMetaData))

    doNothing().when(metadataService).saveMetaDataForDevice(any(), any())

    testSource.runGraph()

    Thread.sleep(20000)
    val captor = ArgumentCaptor.forClass(classOf[String])

    Mockito.verify(mqttConnector, times(2)).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
    val res = captor.getAllValues

    assert(res.size() == 2)
    assert(res.get(0).toString == "{\"frequencyMismatchElements\":[\"waterTankLevel\",\"humidity\"]}")
    assert(res.get(1).toString == "{\"missingElements\":[\"waterTankLevel\"]}")
  }

  @Test
  def streamGraphFlowWithNoMetaData(): Unit = {
    when(metadataService.getMetadataFromRemote(any())).
      thenReturn(Left("Error fetching metadata"))

    when(metadataService.getMetadataForDevice(any())).
      thenReturn(Left("Error fetching metadata"))

    doNothing().when(metadataService).saveMetaDataForDevice(any(), any())

    testSource.runGraph()

    Thread.sleep(20000)
    val captor = ArgumentCaptor.forClass(classOf[String])

    Mockito.verify(mqttConnector, never()).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
    val res = captor.getAllValues

    assert(res.size() == 0)
  }

}

class TestSource extends StreamsService[NotUsed] {
  val msg = "{\"deviceName\": \"device1\", \"tagList\": [{\"ts\": 678, \"tag\": \"humidity\", \"value\": \"value\"}]}"
  val inboundMsg: InboundMsg = JsonUtil.fromJson[InboundMsg](msg)
  override def createSource(): Source[(String, InboundMsg), NotUsed] = {
    val list = List(("deviceId", inboundMsg), ("deviceId", inboundMsg), ("deviceId", inboundMsg))
    Source(list)
  }
}

@Profile(Array("streams-test"))
@Configuration class StreamsTestConfiguration {
  @Bean
  @Primary def streamsApp: StreamsApp = Mockito.mock(classOf[StreamsApp])

  @Bean
  @Primary def metadataService: MetadataService = Mockito.mock(classOf[MetadataService])

  @Bean
  @Primary def mqttConnector: MqttConnector = Mockito.mock(classOf[MqttConnector])

}
