package com.learner.language.domain.feedback

import org.springframework.stereotype.Component

@Component
class FeedbackServiceImpl(
    private val feedbackReader: FeedbackReader
): FeedbackService {
    override fun getMyFeedback(userId: Long, sentenceId: Long): FeedbackInfo {
        val feedback = feedbackReader.getUserFeedback(userId, sentenceId)
        val feedbackInfo = FeedbackInfo(feedback)

        return feedbackInfo
    }
}
