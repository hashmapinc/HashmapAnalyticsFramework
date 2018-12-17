package com.hashmap.dataquality.qualitycheck

import com.hashmap.dataquality.metadata.MetadataFetchService
import com.hashmapinc.tempus.MqttConnector
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(classOf[MockitoJUnitRunner])
class FrequencyQualityCheckSpec {

  private var metadataFetchService: MetadataFetchService = _
  private var mqttConnector: MqttConnector = _
  private var frequencyQualityCheck: FrequencyQualityCheck = _

  @Before
  def setup(): Unit = {
    metadataFetchService = mock(classOf[MetadataFetchService])
    mqttConnector = mock(classOf[MqttConnector])
    frequencyQualityCheck = new FrequencyQualityCheck(metadataFetchService, mqttConnector)
  }

  @Test
  def noFrequencyMismatch(): Unit = {

  }

}
