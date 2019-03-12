package com.hashmap.dataquality.qualitycheck

//import com.hashmap.dataquality.data.{InboundMsg, TsKvData}
import com.hashmap.dataquality.data.Msgs.{InboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.{MetadataService, TagMetaData}
import com.hashmapinc.tempus.MqttConnector
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Mockito.{mock, when}
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito}

import scala.collection.mutable.ListBuffer

@RunWith(classOf[MockitoJUnitRunner])
class TagPresenceQualityCheckTest {

  private var metadataFetchService: MetadataService = _
  private var mqttConnector: MqttConnector = _
  private var tagQualityCheck: TagPresenceQualityCheck = _

  @Before
  def setup(): Unit = {
    metadataFetchService = mock(classOf[MetadataService])
    mqttConnector = mock(classOf[MqttConnector])
    tagQualityCheck = new TagPresenceQualityCheck(metadataFetchService, mqttConnector)
  }

  @Test
  def noTagsMissing(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "50"), TagMetaData("tag2", "50"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1000, "tag1", "value1"), TsKvData(1001, "tag2", "value1"), TsKvData(1050, "tag1", "value2"), TsKvData(1100, "tag2", "value2"))
    val givenPayload = InboundMsg(Option.empty[String], givenDeviceName, givenTsKvData.to[ListBuffer])
    val captor = ArgumentCaptor.forClass(classOf[String])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    tagQualityCheck.check(givenDeviceId, givenPayload)

    //then
    Mockito.verify(mqttConnector, Mockito.never()).publish(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
  }

  @Test
  def oneTagMissing(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "50"), TagMetaData("tag2", "50"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1001, "tag2", "value1"), TsKvData(1100, "tag2", "value2"))
    val givenPayload = InboundMsg(Option.empty[String], givenDeviceName, givenTsKvData.to[ListBuffer])
    val captor = ArgumentCaptor.forClass(classOf[String])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    tagQualityCheck.check(givenDeviceId, givenPayload)
    Mockito.verify(mqttConnector).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())

    //then
    assert(captor.getValue.toString == "{\"missingElements\":[\"tag1\"]}")
  }

  @Test
  def someTagsMissing(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "50"), TagMetaData("tag2", "50"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1000, "tag3", "value1"), TsKvData(1001, "tag4", "value1"), TsKvData(1200, "tag4", "value2"))
    val givenPayload = InboundMsg(Option.empty[String], givenDeviceName, givenTsKvData.to[ListBuffer])
    val captor = ArgumentCaptor.forClass(classOf[String])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    tagQualityCheck.check(givenDeviceId, givenPayload)
    Mockito.verify(mqttConnector).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())

    //then
    assert(captor.getValue.toString == "{\"missingElements\":[\"tag1\",\"tag2\"]}")
  }
}
