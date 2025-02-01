package com.learner.language.domain.sentence

import com.learner.language.domain.user.User

class SentenceCommand {
    data class Register(
        val userText: String,
        val userId: Long
    ) {
        fun toEntity(user: User) : Sentence {
            return Sentence(
                userText = userText,
                user = user
            )
        }
    }

}
