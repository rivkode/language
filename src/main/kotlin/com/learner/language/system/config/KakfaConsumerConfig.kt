package com.learner.language.system.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties
) {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val props: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to true,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            setConcurrency(3) // 단, 이는 토픽의 파티션 수에 따라 제한됩니다. 파티션 수보다 많은 concurrency는 의미가 없습니다.
            containerProperties.pollTimeout = 3000
        }
    }

    @Bean
    fun batchKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            isBatchListener = true
            setConcurrency(2)
            containerProperties.pollTimeout = 50000
        }
    }

//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, Any> {
//        val props: Map<String, Any> = mapOf(
//            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
//            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
//            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false, // 수동 커밋
//            JsonDeserializer.TRUSTED_PACKAGES to "*"
//        )
//        return DefaultKafkaConsumerFactory(props)
//    }
//
//    @Bean
//    fun batchKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
//        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
//            consumerFactory = consumerFactory()
//            isBatchListener = true
//            setConcurrency(2)
//            containerProperties.pollTimeout = 5000
//            setContainerCustomizer {
//                it.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
//            }
//        }
//    }
}
