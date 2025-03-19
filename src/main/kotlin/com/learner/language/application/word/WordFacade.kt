package com.learner.language.application.word

import com.learner.language.domain.word.*
import org.springframework.stereotype.Service

@Service
class WordFacade(
    private val wordReader: WordReader,
    private val wordWriter: WordWriter,
    private val wordService: WordService

) {
    fun retrieveMyWord(userId: Long): List<WordInfo> {
        val myWordListInfo = wordService.getMyWordList(userId)

        return myWordListInfo
    }

    fun retrieveChoice(part: Part, userId: Long, lastWordId: Long?): List<WordInfo> {
        val selectionWords = wordService.getChoiceWord(part, userId, lastWordId)

        return selectionWords
    }

    fun registerChoiceWord(command: WordCommand.RegisterChoiceWord): WordInfo {
        val registeredSelectionWord = wordService.saveChoiceWord(command)

        return registeredSelectionWord
    }
}
