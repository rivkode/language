package com.learner.language.domain.chat.question

import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.user.UserReader
import org.springframework.stereotype.Component

@Component
class QuestionServiceImpl(
    private val questionWriter: QuestionWriter,
    private val questionReader: QuestionReader,
    private val userReader: UserReader,
    private val aiChatService: AiChatService
) : QuestionService {
    override fun saveQuestion(command: QuestionCommand.Register, userId: Long): QuestionInfo {
        val user = userReader.getUserById(userId)
        val savedQuestion = questionWriter.save(command.toEntity(user))
        val questionInfo = QuestionInfo(savedQuestion)

        return questionInfo
    }

    override fun getMyQuestion(userId: Long, questionId: Long): QuestionInfo {
        val question = questionReader.getUserQuestion(userId, questionId)
        val questionInfo = QuestionInfo(question)

        return questionInfo
    }

    override fun getMyQuestionList(userId: Long): List<QuestionInfo> {
        val questionList = questionReader.getQuestionListByUserId(userId)
        val questionInfoList = QuestionInfo.from(questionList)

        return questionInfoList
    }

}
