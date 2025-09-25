package com.learner.language.interfaces.sentence

import com.learner.language.domain.sentence.SentenceCommand
import com.learner.language.domain.sentence.SentenceInfo
import jakarta.validation.constraints.NotEmpty

class SentenceDto {
    data class RegisterRequest(
        @NotEmpty(message = "userText는 입력이 필수입니다.")
        val userSentence: String,
        val noun: String,
        val verb: String,
        val adj: String,
        val wordIds: List<Long>
    ) {
        fun toCommand(): SentenceCommand.Register {
            return SentenceCommand.Register(
                userSentence = userSentence,
                noun = noun,
                verb = verb,
                adj= adj,
                wordIds = wordIds
            )
        }
    }

    data class RegisterResponse(
        val sentenceInfo: SentenceInfo
    )

    data class RetrieveListResponse(
        val sentenceInfoList: List<SentenceInfo>
    )

    data class RetrieveResponse(
        val sentenceInfo: SentenceInfo
    )

}
