package com.learner.language.domain.question

interface QuestionService {
    fun saveQuestion(command: QuestionCommand.Register, userId: Long) : QuestionInfo
    fun getMyQuestionList(userId: Long): List<QuestionInfo>
    fun getMyQuestion(userId: Long, questionId: Long): QuestionInfo

}
