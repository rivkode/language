package com.learner.language.domain.event

import java.time.LocalDateTime

class FeedbackSentenceEvent(
    val sentenceId: Long,
    override val userId: Long,
    override val eventType: String,
    override val eventTime: LocalDateTime,

    ) : Event(
    userId = userId,
    eventType = eventType,
    eventTime = eventTime
)
