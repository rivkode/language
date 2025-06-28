package com.learner.language.domain.chat.answer

import java.util.concurrent.CompletableFuture

interface AnswerWriter {
    fun save(answer: Answer) : Answer
    fun asyncSave(answer: Answer) : CompletableFuture<Answer>
}
