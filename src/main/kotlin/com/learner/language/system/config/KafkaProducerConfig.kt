package com.learner.language.system.config

import com.learner.language.domain.event.Event
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties
) {
    @Bean
    fun producerFactory(): ProducerFactory<String, Event> {
        val configProps: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Event> {
        return KafkaTemplate(producerFactory())
    }

//    @Bean
//    fun producerFactory(): ProducerFactory<String, MyMessage> {
//        val configProps: Map<String, Any> = mapOf(
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
//            ProducerConfig.ACKS_CONFIG to "-1",
//            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
//        )
//        return DefaultKafkaProducerFactory(configProps)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, MyMessage> {
//        return KafkaTemplate(producerFactory())
//    }
}
