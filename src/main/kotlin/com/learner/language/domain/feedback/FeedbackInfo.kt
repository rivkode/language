package com.learner.language.domain.feedback

data class FeedbackInfo(
    val userId: Long,
    val aiFeedback: String
) {
    constructor(feedback: Feedback) : this(
        userId = feedback.user.id,
        aiFeedback = feedback.aiFeedback
    )
}
