package com.learner.language.common.kafka

import com.learner.language.domain.event.Event
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class EventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Event>
) {
    fun send(topic: String, event: Event) {
        kafkaTemplate.send(topic, event)
    }
}
