package com.learner.language.interfaces.sentence

import com.learner.language.domain.sentence.SentenceCommand
import com.learner.language.domain.sentence.SentenceInfo
import jakarta.validation.constraints.NotEmpty

class SentenceDto {
    data class RegisterRequest(
        @NotEmpty(message = "userText는 입력이 필수입니다.")
        val userText: String,
        @NotEmpty(message = "userId는 입력이 필수 입니다")
        val userId: Int,
    ) {
        fun toCommand(): SentenceCommand.Register {
            return SentenceCommand.Register(
                userText = userText,
                userId = userId
            )
        }
    }

    data class RegisterResponse(
        val sentenceInfo: SentenceInfo
    )
}
