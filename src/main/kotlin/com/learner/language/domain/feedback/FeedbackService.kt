package com.learner.language.domain.feedback

interface FeedbackService {
    fun getMyFeedback(userId: Long, sentenceId: Long): FeedbackInfo
}
