package com.learner.language.domain.answer

interface AnswerReader {
    fun getUserAnswer(userId: Long, questionId: Long): Answer
}
