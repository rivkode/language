package com.learner.language.domain.feedback

class FeedbackCommand {
    data class Generate(
        val userId: Long,
        val userSentence: String,
        val noun: String,
        val verb: String,
        val adj: String,
        val sentenceId: Long
    )
}