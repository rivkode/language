package com.learner.language.domain.chat.answer

import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.chat.question.QuestionReader
import com.learner.language.domain.user.UserReader
import com.learner.language.interfaces.chat.answer.AnswerDto
import org.springframework.stereotype.Component

@Component
class AnswerServiceImpl(
    private val userReader: UserReader,
    private val questionReader: QuestionReader,
    private val answerReader: AnswerReader,
    private val aiChatService: AiChatService
): AnswerService {
    override fun getMyAnswer(userId: Long, questionId: Long): AnswerInfo {
        val answer = answerReader.getUserAnswer(userId, questionId)
        val answerInfo = AnswerInfo(answer)

        return answerInfo
    }

    override fun generateAnswer(userId: Long, registerRequest: AnswerDto.RegisterRequest): AnswerInfo {
        val user = userReader.getUserById(userId)
        val quesiton = questionReader.getQuestionId(registerRequest.questionId)
//        val answerInfo = aiChatService.generateChat(registerRequest, user, quesiton)
        val answerInfo = AnswerInfo(userId, "")

        return answerInfo
    }
}
