package com.learner.language.domain.question

interface QuestionReader {
    fun getQuestionListByUserId(userId: Long): List<Question>
    fun getUserQuestion(userId: Long, questionId: Long): Question
}
