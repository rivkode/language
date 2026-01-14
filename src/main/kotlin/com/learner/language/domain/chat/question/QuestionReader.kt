package com.learner.language.domain.chat.question

interface QuestionReader {
    fun getQuestionListByUserId(userId: Long): List<Question>
    fun getUserQuestion(userId: Long, questionId: Long): Question
    fun getQuestionId(questionId: Long): Question
}
