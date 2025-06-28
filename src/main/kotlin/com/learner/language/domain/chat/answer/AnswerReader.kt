package com.learner.language.domain.chat.answer

interface AnswerReader {
    fun getUserAnswer(userId: Long, questionId: Long): Answer
}
