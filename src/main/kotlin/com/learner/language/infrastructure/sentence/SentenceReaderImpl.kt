package com.learner.language.infrastructure.sentence

import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.sentence.SentenceReader
import org.springframework.stereotype.Component

@Component
class SentenceReaderImpl(
    private val sentenceRepository: SentenceRepository
) : SentenceReader {
    override fun getSentenceById(id: Long): Sentence {
        TODO("Not yet implemented")
    }

}