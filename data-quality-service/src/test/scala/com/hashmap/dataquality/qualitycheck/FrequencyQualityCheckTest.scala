package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.data.{InboundMsg, TsKvData}
import com.hashmap.dataquality.metadata.{MetadataService, TagMetaData}
import com.hashmapinc.tempus.MqttConnector
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Mockito.{mock, when}
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito}

import scala.collection.mutable.ListBuffer

@RunWith(classOf[MockitoJUnitRunner])
class FrequencyQualityCheckTest {

  private var metadataFetchService: MetadataService = _
  private var mqttConnector: MqttConnector = _
  private var frequencyQualityCheck: FrequencyQualityCheck = _

  @Before
  def setup(): Unit = {
    metadataFetchService = mock(classOf[MetadataService])
    mqttConnector = mock(classOf[MqttConnector])
    frequencyQualityCheck = new FrequencyQualityCheck(metadataFetchService, mqttConnector, 10)
  }

  @Test
  def noFrequencyMismatch(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "1"), TagMetaData("tag2", "5"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1, "tag1", "value1"), TsKvData(2, "tag2", "value1"), TsKvData(2, "tag1", "value2"), TsKvData(3, "tag1", "value2"), TsKvData(4, "tag1", "value2"), TsKvData(5, "tag1", "value2"))
    val givenPayload = InboundMsg(givenDeviceName, givenTsKvData.to[ListBuffer])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    frequencyQualityCheck.check(givenDeviceId, givenPayload)

    //then
    Mockito.verify(mqttConnector, Mockito.never()).publish(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
  }

  @Test
  def oneFrequencyMismatch(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "5"), TagMetaData("tag2", "10"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1000, "tag1", "value1"), TsKvData(1010, "tag2", "value1"), TsKvData(1020, "tag2", "value2"))
    val givenPayload = InboundMsg(givenDeviceName, givenTsKvData.to[ListBuffer])
    val captor = ArgumentCaptor.forClass(classOf[String])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    frequencyQualityCheck.check(givenDeviceId, givenPayload)
    Mockito.verify(mqttConnector).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())

    //then
    assert(captor.getValue.toString == "{\"frequencyMismatchElements\":[\"tag1\"]}")
  }

  @Test
  def someFrequencyMismatch(): Unit = {
    //given
    val givenDeviceId = "someDeviceId"
    val givenTagMetadata = List(TagMetaData("tag1", "1"), TagMetaData("tag2", "5"))
    val givenDeviceName = "someDeviceName"
    val givenTsKvData = List(TsKvData(1000, "tag1", "value1"), TsKvData(1005, "tag1", "value2"), TsKvData(1011, "tag1", "value2"))
    val givenPayload = InboundMsg(givenDeviceName, givenTsKvData.to[ListBuffer])
    val captor = ArgumentCaptor.forClass(classOf[String])
    when(metadataFetchService.getMetadataForDevice(givenDeviceId)).thenReturn(Right(givenTagMetadata))

    //when
    frequencyQualityCheck.check(givenDeviceId, givenPayload)
    Mockito.verify(mqttConnector).publish(captor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())

    //then
    assert(captor.getValue.toString == "{\"frequencyMismatchElements\":[\"tag1\",\"tag2\"]}")
  }
}
