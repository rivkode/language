package com.learner.language.domain.answer

interface AnswerService {
    fun getMyAnswer(userId: Long, questionId: Long): AnswerInfo
}
