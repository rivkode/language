package com.learner.language.testutils.fixture

import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.user.User

object SentenceFixture {
    fun createSentence(
        userText: String = "I'm a english learner. And I hope to speak english well",
        user: User
    ): Sentence {
        return Sentence(
            userSentence = userText,
            user = user
        )
    }
}
