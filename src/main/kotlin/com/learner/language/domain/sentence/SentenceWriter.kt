package com.learner.language.domain.sentence

interface SentenceWriter {
    fun save(sentence: Sentence) : Sentence

    fun delete(sentence: Sentence)
}