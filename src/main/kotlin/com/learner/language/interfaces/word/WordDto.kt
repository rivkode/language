package com.learner.language.interfaces.word

import com.learner.language.domain.word.ReviewWords
import com.learner.language.domain.word.WordCommand
import com.learner.language.domain.word.WordInfo
import jakarta.validation.constraints.NotEmpty

class WordDto {
    data class RetrieveMyWordRequest(
        @NotEmpty(message = "id는 필수 입력값입니다.")
        val userId: Long,
    ) {
        fun toCommand(): WordCommand.RetrieveMyWord {
            return WordCommand.RetrieveMyWord(
                userId = userId
            )
        }
    }

    data class RetrieveReviewWordResponse(
        val reviewWords: ReviewWords
    )

    data class RetrieveWordInfoListResponse(
        val wordInfoList: List<WordInfo>
    )

    data class RetrieveWordResponse(
        val wordInfo: WordInfo
    )

    data class RegisterChoiceRequest(
        @NotEmpty(message = "id는 필수 입력값입니다.")
        val wordId: Long,
    ) {
        fun toCommand(userId: Long): WordCommand.RegisterChoiceWord {
            return WordCommand.RegisterChoiceWord(
                wordId = wordId,
                userId = userId
            )
        }
    }
}
