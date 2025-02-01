package com.learner.language.domain.sentence

import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.sentence.SentenceRepository
import org.springframework.stereotype.Component

@Component
class SentenceServiceImpl(
    private val sentenceReader: SentenceReader,
    private val sentenceWriter: SentenceWriter,
    private val userReader: UserReader
) : SentenceService {
    override fun registerSentence(sentenceRegisterCommand: SentenceCommand.Register): SentenceInfo {
        val user = userReader.getUserById(sentenceRegisterCommand.userId)
        val sentence = sentenceWriter.save(sentenceRegisterCommand.toEntity(user))
        val sentenceInfo = SentenceInfo(sentence)

        return sentenceInfo
    }


}