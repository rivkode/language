package com.learner.language.domain.sentence

interface SentenceReader {
    fun getSentenceById(id: Long) : Sentence
    fun getSentenceListByUserId(userId: Long): List<Sentence>
    fun getUserSentence(userId: Long, sentenceId: Long): Sentence
}
