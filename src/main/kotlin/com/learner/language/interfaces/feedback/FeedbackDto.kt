package com.learner.language.interfaces.feedback

import com.learner.language.domain.feedback.FeedbackInfo

class FeedbackDto {
    data class RetrieveResponse(
        val feedbackInfo: FeedbackInfo
    )

}
