package com.hashmap.dataquality.config

import com.hashmapinc.tempus.MqttConnector
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class MqttConnectorConfig {

  @Value("${tempus.mqtt-url}") private val MQTT_URL: String = ""

  @Value("${tempus.gateway-access-token}") private val ACCESS_TOKEN: String = ""

  @Bean
  def mqttConnectorBean(): MqttConnector = {
    new MqttConnector(MQTT_URL, ACCESS_TOKEN)
  }

}
