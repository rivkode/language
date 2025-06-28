package com.learner.language.interfaces.chat.question

import com.learner.language.domain.chat.question.QuestionCommand
import com.learner.language.domain.chat.question.QuestionInfo
import jakarta.validation.constraints.NotEmpty

class QuestionDto {
    data class RegisterRequest(
        @NotEmpty(message = "userQuestion should be put")
        val userQuestion: String
    ) {
        fun toCommand(): QuestionCommand.Register {
            return QuestionCommand.Register(
                userQuestion = userQuestion
            )
        }
    }

    data class RegisterResponse(
        val questionInfo: QuestionInfo
    )

    data class RetrieveResponse(
        val questionInfo: QuestionInfo
    )

    data class RetrieveListResponse(
        val questionInfoList: List<QuestionInfo>
    )
}
