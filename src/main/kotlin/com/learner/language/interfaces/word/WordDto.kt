package com.learner.language.interfaces.word

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

    data class RetrieveMyWordResponse(
        val wordInfoList: List<WordInfo>
    )
}
