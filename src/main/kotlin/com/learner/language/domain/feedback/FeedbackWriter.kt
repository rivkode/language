package com.learner.language.domain.feedback

import java.util.concurrent.CompletableFuture

interface FeedbackWriter {
    fun save(feedback: Feedback) : Feedback

    fun asyncSave(feedback: Feedback) : CompletableFuture<Feedback>
}
