package com.learner.language.domain.feedback

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.event.FeedbackSentenceEvent
import com.learner.language.domain.sentence.SentenceReader
import com.learner.language.domain.user.UserReader
import com.learner.language.interfaces.feedback.FeedbackDto
import org.springframework.stereotype.Component

@Component
class FeedbackServiceImpl(
    private val feedbackReader: FeedbackReader,
    private val feedbackWriter: FeedbackWriter,
    private val userReader: UserReader,
    private val sentenceReader: SentenceReader,
    private val aiChatService: AiChatService
): FeedbackService {
    override fun getMyFeedback(userId: Long, sentenceId: Long): FeedbackInfo {
        val feedback = feedbackReader.getUserFeedback(userId, sentenceId)
        val aiFeedback = readAiFeedback(feedback.aiFeedback)
        val feedbackInfo = FeedbackInfo(feedback, aiFeedback)

        return feedbackInfo
    }

    override fun eventProcess(event: FeedbackSentenceEvent) {

    }

    override fun generateFeedback(command: FeedbackCommand.Generate): FeedbackInfo {
        val user = userReader.getUserById(command.userId)
        val sentence = sentenceReader.getSentenceById(command.sentenceId)
        val feedback = aiChatService.generateFeedback(user, sentence, command)
        val savedFeedback = feedbackWriter.save(feedback)
        val aiFeedback = readAiFeedback(feedback.aiFeedback)
        val feedbackInfo = FeedbackInfo(savedFeedback, aiFeedback)

        return feedbackInfo
    }

    private fun readAiFeedback(aiFeedback: String): FeedbackInfo.AiFeedback {
        val mapper = jacksonObjectMapper()
        val cleanedAiFeedback = aiFeedback
            .replace("```json", "")
            .replace("```", "")
            .trim()
        val aiFeedback: FeedbackInfo.AiFeedback = mapper.readValue(cleanedAiFeedback)

        return aiFeedback
    }
}
