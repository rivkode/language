package com.learner.language.application.answer

import com.learner.language.domain.answer.AnswerInfo
import com.learner.language.domain.answer.AnswerService
import org.springframework.stereotype.Service

@Service
class AnswerFacade(
    private val answerService: AnswerService
) {
    fun retrieveAnswer(userId: Long, questionId: Long): AnswerInfo {
        val answerInfo = answerService.getMyAnswer(userId, questionId)

        return answerInfo
    }
}
