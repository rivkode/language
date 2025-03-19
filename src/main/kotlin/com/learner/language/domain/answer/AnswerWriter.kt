package com.learner.language.domain.answer

import java.util.concurrent.CompletableFuture

interface AnswerWriter {
    fun save(answer: Answer) : Answer
    fun asyncSave(answer: Answer) : CompletableFuture<Answer>
}
