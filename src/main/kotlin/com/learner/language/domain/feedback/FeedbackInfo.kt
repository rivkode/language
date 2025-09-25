package com.learner.language.domain.feedback

data class FeedbackInfo(
    val userId: Long,
    val aiFeedback: AiFeedback,
    val aiFeedbackText: String
) {
    constructor(feedback: Feedback, aiFeedback: AiFeedback) : this(
        userId = feedback.user.id,
        aiFeedback = aiFeedback,
        aiFeedbackText = feedback.aiFeedback
    )

    data class AiFeedback(
        val grammer: FeedbackDetail,
        val vocabulary: FeedbackDetail,
        val sentenceStructure: FeedbackDetail
    )

    data class FeedbackDetail(
        val words: List<WordFeedback>,
        val overall: String
    )

    data class WordFeedback(
        val wrong_word: String,
        val fixed_word: String,
        val issue: String
    )
}
