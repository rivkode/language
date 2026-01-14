package com.learner.language.domain.feedback

import com.learner.language.domain.event.FeedbackSentenceEvent
import com.learner.language.interfaces.feedback.FeedbackDto

interface FeedbackService {
    fun getMyFeedback(userId: Long, sentenceId: Long): FeedbackInfo
    fun eventProcess(event: FeedbackSentenceEvent)
    fun generateFeedback(command: FeedbackCommand.Generate): FeedbackInfo
}
