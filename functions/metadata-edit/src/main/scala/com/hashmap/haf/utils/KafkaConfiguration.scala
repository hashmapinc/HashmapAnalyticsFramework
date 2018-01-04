package com.hashmap.haf.utils

import java.util.Properties

object KafkaConfiguration {
	def kafkaProducerProperties(kafkaBroker: String, id: String): Properties = {
		val props = new Properties()
		props.put("bootstrap.servers", kafkaBroker)
		props.put("client.id", id)
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
		props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer")
		props
	}
}
