package com.learner.language.domain.word

interface WordWriter {
    fun registerWord(word: Word) : Word
    fun updateWord(word: Word) : Word

}
