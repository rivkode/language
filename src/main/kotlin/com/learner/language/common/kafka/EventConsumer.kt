package com.learner.language.common.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.domain.event.Event
import com.learner.language.domain.event.exception.EventBadRequestException
import org.springframework.stereotype.Component
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener

@Component
class EventConsumer(
    private val eventService: EventService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["events"], groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    fun consume(message: ConsumerRecord<String, Event>) {
        val eventString = message.value() ?: throw EventBadRequestException("message is null")
        eventService.process(eventString)
        println("Consumed message: ${message.value()}")
    }

//    @KafkaListener(topics = ["my-topic"], groupId = "test-group-batch", containerFactory = "batchKafkaListenerContainerFactory")
//    fun consumeBatch(messages: List<ConsumerRecord<String, MyMessage>>) {
//        println("Consumed batch of messages: ${messages.size}")
//        messages.forEachIndexed { index, record ->
//            val message = record.value()
//            println("Message ${index + 1}:")
//            println("  Topic: ${record.topic()}")
//            println("  Partition: ${record.partition()}")
//            println("  Offset: ${record.offset()}")
//            println("  Key: ${record.key()}")
//            println("  Value:")
//            println("    ID: ${message.id}")
//            println("    Age: ${message.age}")
//            println("    Name: ${message.name}")
//            println("    Content: ${message.content}")
//            println("-----------------------------")
//        }
//        println("Batch processing completed")
//    }
//
//    @KafkaListener(topics = ["my-topic"], groupId = "test-group-batch", containerFactory = "batchKafkaListenerContainerFactory")
//    fun consumeBatch(messages: List<ConsumerRecord<String, MyMessage>>, acknowledgment: Acknowledgment) {
//        println("Consumed batch of messages: ${messages.size}")
//        messages.forEachIndexed { index, record ->
//            val message = record.value()
//            println("Message ${index + 1}:")
//            println("  Topic: ${record.topic()}")
//            println("  Partition: ${record.partition()}")
//            println("  Offset: ${record.offset()}")
//            println("  Key: ${record.key()}")
//            println("  Value:")
//            println("    ID: ${message.id}")
//            println("    Age: ${message.age}")
//            println("    Name: ${message.name}")
//            println("    Content: ${message.content}")
//            println("-----------------------------")
//        }
//        println("Batch processing completed")
//        acknowledgment.acknowledge() // 수동커밋
//    }
}