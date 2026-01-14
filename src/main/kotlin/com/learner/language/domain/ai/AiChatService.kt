package com.learner.language.domain.ai

import com.learner.language.domain.chat.ChatCommand
import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatRoom
import com.learner.language.domain.chat.ChatWriter
import com.learner.language.domain.feedback.Feedback
import com.learner.language.domain.feedback.FeedbackCommand
import com.learner.language.domain.feedback.FeedbackWriter
import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.user.User
import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service


private val logger = KotlinLogging.logger {}
@Service
class AiChatService(
    private val chatClient: ChatClient,
    private val aiPromptService: AiPromptService,
    private val feedbackWriter: FeedbackWriter,
    private val chatWriter: ChatWriter,
) {
    fun generate(aiChatRequest: AiChatCommand.AiRequest): AiChatCommand.ChatResponse {
        val prompt = aiPromptService.createPrompt(aiChatRequest)
        val result = chatClient.prompt(prompt).call().content()
            ?: throw BadRequestException(ErrorCode.BAD_REQUEST, "result is null")
        return AiChatCommand.ChatResponse(result)
    }

    fun generateFeedback(user: User, sentence: Sentence, command: FeedbackCommand.Generate): Feedback {
        val aiRequest = AiChatCommand.AiRequest.FeedbackRequest(
            input = command.userSentence,
            noun = command.noun,
            verb = command.verb,
            adj = command.adj,
        )

        val response = generate(aiRequest)
        logger.info { "ai feedback response generateFeedback: ${response.response}" }
        val feedback = Feedback(aiFeedback = response.response, user = user, sentence = sentence)

        return feedback
    }

    fun generateChat(command: ChatCommand.Generate, user: User, chatRoom: ChatRoom, chatHistory: String, nextSequence: Int): ChatMessage {
        val aiRequest = AiChatCommand.AiRequest.ChatRequest(
            input = chatHistory
        )
        val response = generate(aiRequest)
        logger.info { "ai answer response generateChat: ${response.response}" }
        val chatMessage = command.toEntity(user, chatRoom, response.response, nextSequence)


        return chatMessage
    }

    fun generateChatRoomName(command: ChatCommand.Register): AiChatCommand.ChatResponse {
        val aiRequest = AiChatCommand.AiRequest.ChatRoomNameRequest(
            input = command.message
        )
        val response = generate(aiRequest)
        logger.info { "ai answer response generateChatRoomName: ${response.response}" }

        return response
    }
}
