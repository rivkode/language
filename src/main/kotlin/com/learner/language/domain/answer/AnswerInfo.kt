package com.learner.language.domain.answer

data class AnswerInfo(
    val userId: Long,
    val aiAnswer: String
) {
    constructor(answer: Answer) : this(
        userId = answer.user.id,
        aiAnswer = answer.aiAnswer
    )
}
