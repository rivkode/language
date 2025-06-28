package com.learner.language.domain.chat.answer

import com.learner.language.interfaces.chat.answer.AnswerDto

interface AnswerService {
    fun getMyAnswer(userId: Long, questionId: Long): AnswerInfo
    fun generateAnswer(userId: Long, registerRequest: AnswerDto.RegisterRequest): AnswerInfo
}
