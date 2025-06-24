package com.learner.language.domain.event

import java.time.LocalDateTime

open class Event(
    open val userId: Long,
    open val eventType: String,
    open val eventTime: LocalDateTime,
)