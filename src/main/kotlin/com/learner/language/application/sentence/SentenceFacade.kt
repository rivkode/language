package com.learner.language.application.sentence

import com.learner.language.domain.sentence.*
import org.springframework.stereotype.Service

@Service
class SentenceFacade(
    private val sentenceReader: SentenceReader,
    private val sentenceWriter: SentenceWriter,
    private val sentenceService: SentenceService,
) {
    fun registerSentence(command: SentenceCommand.Register) : SentenceInfo
    {
        val sentenceInfo = sentenceService.registerSentence(command)

        return sentenceInfo
    }

}
