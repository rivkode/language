package com.learner.language.domain.feedback

interface FeedbackReader {
    fun getUserFeedback(userId: Long, sentenceId: Long): Feedback
}
