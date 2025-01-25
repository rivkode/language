package com.learner.language.domain.word

interface WordService {
    fun getMyWordList(wordRetrieveCommand: WordCommand.RetrieveMyWord) : List<WordInfo>

    fun registerWord(wordRegisterCommand: WordCommand.RegisterWord) : WordInfo

}
