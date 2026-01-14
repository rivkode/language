package com.learner.language.domain.sentence

interface SentenceService {
    fun saveSentence(command: SentenceCommand.Register, userId: Long) : SentenceInfo
    fun getMySentenceList(userId: Long): List<SentenceInfo>
    fun getMySentence(userId: Long, sentenceId: Long): SentenceInfo

}
