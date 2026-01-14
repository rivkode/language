package com.learner.language.application.sentence

import com.learner.language.domain.sentence.*
import org.springframework.stereotype.Service

@Service
class SentenceFacade(
    private val sentenceService: SentenceService,
) {
    fun registerSentence(command: SentenceCommand.Register, userId: Long) : SentenceInfo
    {
        val sentenceInfo = sentenceService.saveSentence(command, userId)

        return sentenceInfo
    }

    fun retrieveMySentenceList(userId: Long): List<SentenceInfo> {
        val sentenceInfoList = sentenceService.getMySentenceList(userId)

        return sentenceInfoList
    }

    fun retrieveMySentence(userId: Long, sentenceId: Long): SentenceInfo {
        val sentenceInfo = sentenceService.getMySentence(userId, sentenceId)

        return sentenceInfo
    }

}
