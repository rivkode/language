package com.learner.language.domain.word

import com.learner.language.domain.word.match.WordUserMatch

class WordCommand{

    data class RetrieveMyWord(
        val userId: Long
    )

    data class RegisterWord(
        val label: String,
        val description: String
    ) {
        fun toEntity() : Word {
            return Word(
                label = label,
                description = description
            )
        }
    }

}
