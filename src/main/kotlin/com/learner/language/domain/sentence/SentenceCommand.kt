package com.learner.language.domain.sentence

import com.learner.language.domain.user.User

class SentenceCommand {
    data class Register(
        val userSentence: String,
        val noun: String,
        val verb: String,
        val adj: String,
        val wordIds: List<Long> = listOf()
    ) {
        fun toEntity(user: User) : Sentence {
            return Sentence(
                userSentence = userSentence,
                user = user
            )
        }
    }

}
