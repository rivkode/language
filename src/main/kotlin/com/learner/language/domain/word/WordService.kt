package com.learner.language.domain.word

interface WordService {
    fun getMyWordList(userId: Long) : List<WordInfo>
    fun registerWord(wordRegisterCommand: WordCommand.RegisterWord) : WordInfo
    fun getChoiceWord(part: Part, userId: Long, lastWordId: Long?): List<WordInfo>
    fun saveChoiceWord(command: WordCommand.RegisterChoiceWord): WordInfo

}
