package com.learner.language.domain.ai

import com.learner.language.domain.answer.Answer
import com.learner.language.domain.answer.AnswerWriter
import com.learner.language.domain.feedback.Feedback
import com.learner.language.domain.feedback.FeedbackWriter
import com.learner.language.domain.question.Question
import com.learner.language.domain.question.QuestionCommand
import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.sentence.SentenceCommand
import com.learner.language.domain.user.User
import com.learner.language.interfaces.ai.AiChatDto
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service


private val logger = KotlinLogging.logger {}
@Service
class AiChatService(
    private val chatClient: ChatClient,
    private val aiPromptService: AiPromptService,
    private val feedbackWriter: FeedbackWriter,
    private val answerWriter: AnswerWriter
) {
    fun generate(aiChatRequest: AiChatDto.AiRequest): AiChatDto.AiChatResponse {
        val prompt = aiPromptService.createPrompt(aiChatRequest)
        return AiChatDto.AiChatResponse(chatClient.prompt(prompt).call().content())
    }

    @Async
    fun generateFeedback(command: SentenceCommand.Register, user: User, sentence: Sentence) {
        val aiRequest = AiChatDto.AiRequest.AiFeedbackRequest(
            userInput = command.userSentence,
            noun = command.noun,
            verb = command.verb,
            adj = command.adj,
        )

        val response = generate(aiRequest)
        logger.info { "ai feedback response: ${response.response}" }
        val feedback = Feedback(aiFeedback = response.response, user = user, sentence = sentence)

        feedbackWriter.asyncSave(feedback)
    }

    @Async
    fun generateAnswer(command: QuestionCommand.Register, user: User, question: Question) {
        val aiRequest = AiChatDto.AiRequest.AiAnswerRequest(
            userInput = command.userQuestion
        )

        val response = generate(aiRequest)
        logger.info { "ai answer response: ${response.response}" }
        val answer = Answer(aiAnswer = response.response, user = user, question = question)

        answerWriter.asyncSave(answer)
    }
}
