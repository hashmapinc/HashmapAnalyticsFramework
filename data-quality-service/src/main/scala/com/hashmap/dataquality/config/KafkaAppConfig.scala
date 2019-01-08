package com.hashmap.dataquality.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaAppConfig {
  @Value("${kafka-app.time-window}") val TIME_WINDOW: Long = 0
}
