package com.learner.language.application.chat.answer

import com.learner.language.domain.chat.answer.AnswerInfo
import com.learner.language.domain.chat.answer.AnswerService
import com.learner.language.interfaces.chat.answer.AnswerDto
import org.springframework.stereotype.Service

@Service
class AnswerFacade(
    private val answerService: AnswerService
) {
    fun retrieveAnswer(userId: Long, questionId: Long): AnswerInfo {
        val answerInfo = answerService.getMyAnswer(userId, questionId)

        return answerInfo
    }

    fun generateAnswer(userId: Long, registerRequest: AnswerDto.RegisterRequest): AnswerInfo {
        val answerInfo = answerService.generateAnswer(userId, registerRequest)

        return answerInfo
    }
}
