package com.learner.language.application.chat.question

import com.learner.language.domain.chat.question.QuestionCommand
import com.learner.language.domain.chat.question.QuestionInfo
import com.learner.language.domain.chat.question.QuestionService
import org.springframework.stereotype.Service

@Service
class QuestionFacade(
    private val questionService: QuestionService,
) {
    fun registerQuestion(command: QuestionCommand.Register, userId: Long) : QuestionInfo {
        val questionInfo = questionService.saveQuestion(command, userId)

        return questionInfo
    }

    fun retrieveMyQuestionList(userId: Long): List<QuestionInfo> {
        val questionInfoList = questionService.getMyQuestionList(userId)

        return questionInfoList
    }

    fun retrieveMyQuestion(userId: Long, questionId: Long) : QuestionInfo {
        val questionInfo = questionService.getMyQuestion(userId, questionId)

        return questionInfo
    }
}
