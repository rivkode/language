package com.learner.language.domain.word

import com.learner.language.domain.user.User
import com.learner.language.domain.word.usermatch.WordUserMatch

class WordCommand{

    data class RetrieveMyWord(
        val userId: Long
    )

    data class RetrieveChoiceWord(
        val userId: Long,
        val wordLastId: Long?
    )

    data class RegisterWord(
        val label: String,
        val enMeaning: String,
        val krExample: String,
        val enExample: String,
        val ipa: String,
        val part: Part,
        val level: CefrLevel
    ) {
        fun toEntity() : Word {
            return Word(
                label = label,
                enMeaning = enMeaning,
                krExample = krExample,
                enExample =  enExample,
                ipa = ipa,
                part = part,
                level = level
            )
        }
    }

    data class RegisterChoiceWord(
        val wordId: Long,
        val userId: Long
    ) {
        fun toEntity(word: Word, user: User) : WordUserMatch {
            return WordUserMatch(
                word = word,
                user = user
            )
        }
    }

}
