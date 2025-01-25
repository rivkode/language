package com.learner.language.application.word

import com.learner.language.domain.word.*
import org.springframework.stereotype.Service

@Service
class WordFacade(
    private val wordReader: WordReader,
    private val wordWriter: WordWriter,
    private val wordService: WordService

) {
    fun retrieveMyWord(command: WordCommand.RetrieveMyWord): List<WordInfo> {
        val myWordListInfo = wordService.getMyWordList(command)

        return myWordListInfo
    }
}
