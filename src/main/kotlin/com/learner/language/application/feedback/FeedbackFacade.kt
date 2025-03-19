package com.learner.language.application.feedback

import com.learner.language.domain.feedback.FeedbackInfo
import com.learner.language.domain.feedback.FeedbackService
import org.springframework.stereotype.Service

@Service
class FeedbackFacade(
    private val feedbackService: FeedbackService
) {
    fun retrieveFeedback(userId: Long, sentenceId: Long): FeedbackInfo {
        val feedbackInfo = feedbackService.getMyFeedback(userId, sentenceId)

        return feedbackInfo
    }
}
