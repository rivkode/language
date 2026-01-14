package com.learner.language.infrastructure.sentence

import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.sentence.SentenceWriter
import org.springframework.stereotype.Component

@Component
class SentenceWriterImpl(
    private val sentenceRepository: SentenceRepository
) : SentenceWriter {
    override fun save(sentence: Sentence) : Sentence {
        return sentenceRepository.save(sentence)
    }

    override fun delete(sentence: Sentence) {
        sentenceRepository.delete(sentence)
    }
}