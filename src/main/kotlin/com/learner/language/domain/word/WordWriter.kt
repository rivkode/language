package com.learner.language.domain.word

interface WordWriter {
    fun save(word: Word) : Word
    fun updateWord(word: Word) : Word

}
