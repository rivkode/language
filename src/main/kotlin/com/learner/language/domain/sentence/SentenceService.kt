package com.learner.language.domain.sentence

interface SentenceService {
    fun registerSentence(sentenceRegisterCommand: SentenceCommand.Register) : SentenceInfo

}
