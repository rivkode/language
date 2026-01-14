package com.learner.language.application.feedback

import com.learner.language.domain.feedback.FeedbackCommand
import com.learner.language.domain.feedback.FeedbackInfo
import com.learner.language.domain.feedback.FeedbackService
import com.learner.language.interfaces.feedback.FeedbackDto
import org.springframework.stereotype.Service

@Service
class FeedbackFacade(
    private val feedbackService: FeedbackService
) {
    fun retrieveFeedback(userId: Long, sentenceId: Long): FeedbackInfo {
        val feedbackInfo = feedbackService.getMyFeedback(userId, sentenceId)

        return feedbackInfo
    }

    fun registerFeedback(command: FeedbackCommand.Generate): FeedbackInfo {
        val feedbackInfo = feedbackService.generateFeedback(command)

        return feedbackInfo
    }
}
