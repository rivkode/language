package com.learner.language.domain.event

import java.time.LocalDateTime

class WordReviewEvent(
    val wordId: Long,
    val known: Boolean,
    override val userId: Long,
    override val eventType: String,
    override val eventTime: LocalDateTime,

    ) : Event(
    userId = userId,
    eventType = eventType,
    eventTime = eventTime
    )
