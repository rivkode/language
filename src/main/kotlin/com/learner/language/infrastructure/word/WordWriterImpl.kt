package com.learner.language.infrastructure.word

import com.learner.language.domain.word.Word
import com.learner.language.domain.word.WordWriter
import org.springframework.stereotype.Component

@Component
class WordWriterImpl(
    private val wordRepository: WordRepository
) : WordWriter {
    override fun save(word: Word): Word {
        return wordRepository.save(word)
    }

    override fun update(word: Word): Word {
        return wordRepository.save(word)
    }
}
