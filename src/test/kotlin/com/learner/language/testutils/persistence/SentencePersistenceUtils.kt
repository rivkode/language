package com.learner.language.testutils.persistence

import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.user.User
import com.learner.language.infrastructure.sentence.SentenceRepository
import com.learner.language.testutils.fixture.SentenceFixture

class SentencePersistenceUtils(
    private val sentenceRepository: SentenceRepository
) {
    fun saveNewSentence(
        userText: String = "I'm a english learner. And I hope to speak english well",
        user: User
    ): Sentence {
        val sentence = SentenceFixture.createSentence(userText, user)

        return sentenceRepository.save(sentence)
    }
}
