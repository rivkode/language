package com.learner.language.interfaces.question

import com.learner.language.domain.question.QuestionCommand
import com.learner.language.domain.question.QuestionInfo
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
