package com.learner.language.interfaces.answer

import com.learner.language.domain.answer.AnswerInfo

class AnswerDto {
    data class RetrieveResponse(
        val answerInfo: AnswerInfo
    )
}
