package com.learner.language.domain.answer

import org.springframework.stereotype.Component

@Component
class AnswerServiceImpl(
    private val answerReader: AnswerReader
): AnswerService {
    override fun getMyAnswer(userId: Long, questionId: Long): AnswerInfo {
        val answer = answerReader.getUserAnswer(userId, questionId)
        val answerInfo = AnswerInfo(answer)

        return answerInfo
    }
}
