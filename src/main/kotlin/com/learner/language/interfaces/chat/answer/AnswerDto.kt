package com.learner.language.interfaces.chat.answer

import com.learner.language.domain.chat.answer.AnswerInfo
import jakarta.validation.constraints.NotEmpty

class AnswerDto {
    data class RegisterRequest(
        @NotEmpty(message = "userQuestion should be put")
        val userQuestion: String,
        val questionId: Long
    )

    data class RegisterResponse(
        val answerInfo: AnswerInfo
    )



    data class RetrieveResponse(
        val answerInfo: AnswerInfo
    )

}
