package com.learner.language.interfaces.feedback

import com.learner.language.domain.feedback.FeedbackCommand
import com.learner.language.domain.feedback.FeedbackInfo
import jakarta.validation.constraints.NotEmpty

class FeedbackDto {
    data class RegisterRequest(
        val userSentence: String,
        val noun: String,
        val verb: String,
        val adj: String,
        val sentenceId: Long
    ) {
        fun toCommand(userId: Long): FeedbackCommand.Generate {
            return FeedbackCommand.Generate(
                userId = userId,
                userSentence = userSentence,
                noun = noun,
                verb = verb,
                adj = adj,
                sentenceId = sentenceId
            )
        }
    }

    data class RetrieveResponse(
        val feedbackInfo: FeedbackInfo
    )

    data class RegisterResponse(
        val feedbackInfo: FeedbackInfo
    )

}
