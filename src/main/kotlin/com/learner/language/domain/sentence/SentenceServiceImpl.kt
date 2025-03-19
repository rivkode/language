package com.learner.language.domain.sentence

import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.user.UserReader
import org.springframework.stereotype.Component

@Component
class SentenceServiceImpl(
    private val sentenceReader: SentenceReader,
    private val sentenceWriter: SentenceWriter,
    private val userReader: UserReader,
    private val aiChatService: AiChatService,

    ) : SentenceService {
    override fun saveSentence(command: SentenceCommand.Register, userId: Long): SentenceInfo {
        val user = userReader.getUserById(userId)
        val savedSentence = sentenceWriter.save(command.toEntity(user))

        aiChatService.generateFeedback(command, user, savedSentence)

        val sentenceInfo = SentenceInfo(savedSentence)

        return sentenceInfo
    }

    override fun getMySentenceList(userId: Long): List<SentenceInfo> {
        val sentenceList = sentenceReader.getSentenceListByUserId(userId)
        val sentenceListInfo = SentenceInfo.from(sentenceList)

        return sentenceListInfo
    }

    override fun getMySentence(userId: Long, sentenceId: Long): SentenceInfo {
        val sentence = sentenceReader.getUserSentence(userId, sentenceId)
        val sentenceInfo = SentenceInfo(sentence)

        return sentenceInfo
    }

}
