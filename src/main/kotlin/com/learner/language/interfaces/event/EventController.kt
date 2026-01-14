package com.learner.language.interfaces.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.common.kafka.EventProducer
import com.learner.language.domain.event.FeedbackSentenceEvent
import com.learner.language.domain.event.WordKnownEvent
import com.learner.language.domain.event.WordReviewEvent
import com.learner.language.domain.event.exception.EventBadRequestException
import com.learner.language.system.login.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val producer: EventProducer,
    private val objectMapper: ObjectMapper
) {
    val eventClassMap = mapOf(
        "WordReviewEvent" to WordReviewEvent::class.java,
        "WordKnownEvent" to WordKnownEvent::class.java,
        "FeedbackSentenceEvent" to FeedbackSentenceEvent::class.java
    )

    @PostMapping
    fun events(
        @LoginUser userId: Long,
        @RequestBody message: String?
    ): ResponseEntity<String> {
        if (message == null) {
            throw EventBadRequestException("message is null")
        }

        // 이벤트의 타입을 확인해서 정의한 이벤트가 맞는지 체크
        processEvent(message, objectMapper)

        return ResponseEntity.ok("Message sent to Kafka")
    }

    fun processEvent(message: String, objectMapper: ObjectMapper) {
        // mongo db 저장

        // event producer가 produce() 로 이벤트 진행

        val jsonNode = objectMapper.readTree(message)
        val eventType = jsonNode.findValue("eventType").asText()

        val eventClass = eventClassMap[eventType]
        if (eventClass != null) {
            val event = objectMapper.treeToValue(jsonNode, eventClass)

            // event 처리 로직

            producer.send("events", event)
            println("Processed event: $event")
        } else {
            println("Unknown event type: $eventType, message: $message")
            throw EventBadRequestException("Unknown event type: $eventType, message: $message")
        }
    }
}
