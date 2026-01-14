package com.learner.language.domain.word

import com.learner.language.domain.event.Event

interface WordService {
    fun getMyWordList(userId: Long) : List<WordInfo>
    fun registerWord(wordRegisterCommand: WordCommand.RegisterWord) : WordInfo
    fun getChoiceWord(part: Part, userId: Long, lastWordId: Long?): List<WordInfo>
    fun saveChoiceWord(command: WordCommand.RegisterChoiceWord): WordInfo
    fun getReviewWords(userId: Long, wordListId: Int, count: Int): ReviewWords
    fun buildWordInfos(words: List<Word>, count: Int): List<WordInfo>
    fun eventProcess(event: Event)

}
