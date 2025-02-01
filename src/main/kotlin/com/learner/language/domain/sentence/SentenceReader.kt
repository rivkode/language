package com.learner.language.domain.sentence

interface SentenceReader {
    fun getSentenceById(id: Long) : Sentence
}