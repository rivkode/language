package com.learner.language.domain.word

import com.learner.language.domain.word.review.WordReviewCount

interface WordReader {
    fun getWordListByIds(ids: List<Long>) : List<Word>
    fun getWordById(id: Long) : Word
    fun getChoiceWord(part: Part, wordIds: List<Long>, lastId: Long?, pageSize: Int): List<Word>
    fun getNoCountReviewWords(userId: Long, wordListId: Int): List<Word>
    fun getCountReviewWords(userId: Long, wordListId: Int, count: Int): List<Word>
    fun getWordListBySentenceId(sentenceId: Long): List<Word>
}
