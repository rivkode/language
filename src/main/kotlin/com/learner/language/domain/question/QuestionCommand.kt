package com.learner.language.domain.question

import com.learner.language.domain.user.User

class QuestionCommand {
    data class Register(
        val userQuestion: String
    ) {
        fun toEntity(user: User) : Question {
            return Question(
                userQuestion = userQuestion,
                user = user
            )
        }
    }

}
