package com.learner.language.domain.question

data class QuestionInfo(
    val userId: Long,
    val userQuestion: String
) {
    constructor(question: Question) : this(
        userId = question.user.id,
        userQuestion = question.userQuestion
    )

    companion object {
        fun from(questions: List<Question>) : List<QuestionInfo> {
            return questions.map { QuestionInfo(it) }
        }
    }

}
