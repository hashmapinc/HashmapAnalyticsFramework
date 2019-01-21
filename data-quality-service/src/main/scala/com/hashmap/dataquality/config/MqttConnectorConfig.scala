package com.hashmap.dataquality.config

import com.hashmapinc.tempus.MqttConnector
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class MqttConnectorConfig {

  @Value("${tempus.mqtt-bind-address}") private val MQTT_BIND_ADDRESS: String = ""

  @Value("${tempus.mqtt-bind-port}") private val MQTT_BIND_PORT: String = ""

  @Value("${tempus.gateway-access-token}") private val ACCESS_TOKEN: String = ""

  private val MQTT_URL_FORMAT = "tcp://%s:%s"

  @Bean
  def mqttConnectorBean(): MqttConnector = {
    new MqttConnector(String.format(MQTT_URL_FORMAT, MQTT_BIND_ADDRESS, MQTT_BIND_PORT), ACCESS_TOKEN)
  }

}
