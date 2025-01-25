package com.learner.language.domain.word

interface WordReader {
    fun getWordListByIds(ids: List<Long>) : List<Word>
    fun getWordById(id: Long) : Word

}
