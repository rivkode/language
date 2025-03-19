package com.learner.language.testutils.persistence

import com.learner.language.domain.word.Part
import com.learner.language.domain.word.Word
import com.learner.language.infrastructure.word.WordRepository
import com.learner.language.testutils.fixture.WordFixture

class WordPersistenceUtils(
    private val wordRepository: WordRepository
) {
    fun saveNewWord(
        label: String = "hello",
        description: String = "you can say this word When you meet person.",
        part: Part = Part.NOUN
    ): Word {
        val word = WordFixture.createWord(
            label, description, part
        )

        return wordRepository.save(word)
    }

    fun bulkSaveNewWord(
    ) {
        val word1 = WordFixture.createWord(
            "apple", "description", Part.NOUN
        )
        val word2 = WordFixture.createWord(
            "fruit", "description", Part.NOUN
        )
        val word3 = WordFixture.createWord(
            "jake", "description", Part.NOUN
        )
        val word4 = WordFixture.createWord(
            "deep", "description", Part.ADJECTIVE
        )
        val word5 = WordFixture.createWord(
            "happy", "description", Part.ADJECTIVE
        )
        val word6 = WordFixture.createWord(
            "curious", "description", Part.ADJECTIVE
        )
        val word7 = WordFixture.createWord(
            "view", "description", Part.VERB
        )
        val word8 = WordFixture.createWord(
            "operate", "description", Part.VERB
        )
        val word9 = WordFixture.createWord(
            "go", "description", Part.VERB
        )

        wordRepository.save(word1)
        wordRepository.save(word2)
        wordRepository.save(word3)
        wordRepository.save(word4)
        wordRepository.save(word5)
        wordRepository.save(word6)
        wordRepository.save(word7)
        wordRepository.save(word8)
        wordRepository.save(word9)
    }

}
