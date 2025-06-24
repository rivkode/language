package com.learner.language.common.kafka

import com.learner.language.domain.chat.ChatService
import com.learner.language.domain.event.*
import com.learner.language.domain.feedback.FeedbackService
import com.learner.language.domain.word.WordService
import org.springframework.stereotype.Component

@Component
class EventService(
    private val wordService: WordService,
    private val feedbackService: FeedbackService,
    private val chatService: ChatService
) {
    fun process(event: Event) {
        try {
            if (event is WordReviewEvent || event is WordKnownEvent) {
                wordService.eventProcess(event)

            } else if (event is FeedbackSentenceEvent) {
                feedbackService.eventProcess(event)
            } else if (event is ChatEvent) {
                chatService.eventProcess(event)
            } else {
                println("Unknown event type: ${event.eventType}, message: ${event.toString()}")
            }
        } catch (e: Exception) {
            println("Error processing message: $event, error: ${e.message}")
        }
    }
}