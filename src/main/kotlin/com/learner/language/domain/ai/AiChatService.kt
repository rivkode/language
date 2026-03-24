package com.learner.language.domain.ai

import com.learner.language.domain.chat.ChatCommand
import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatRoom
import com.learner.language.domain.chat.ChatWriter
import com.learner.language.domain.chat.SenderType
import com.learner.language.domain.feedback.Feedback
import com.learner.language.domain.feedback.FeedbackCommand
import com.learner.language.domain.feedback.FeedbackWriter
import com.learner.language.domain.prompt.PersonaType
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
        val prompt = aiChatRequest.createPrompt(aiPromptService)
        val result = chatClient.prompt(prompt).call().content()
            ?: throw BadRequestException(ErrorCode.BAD_REQUEST, "result is null")
        return AiChatCommand.ChatResponse(result)
    }

    fun generateGreeting(aiChatRequest: AiChatCommand.AiRequest): AiChatCommand.ChatResponse {
        val prompt = aiChatRequest.createPrompt(aiPromptService)
        val result = chatClient.prompt(prompt).call().content()
            ?: throw BadRequestException(ErrorCode.BAD_REQUEST, "result is null")
        return AiChatCommand.ChatResponse(result)
    }

    fun generateParagraph(aiChatRequest: AiChatCommand.AiRequest): AiChatCommand.ChatResponse {
        val prompt = aiChatRequest.createPrompt(aiPromptService)
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
            personaType = PersonaType.TEACHER
        )

        val response = generate(aiRequest)
        logger.info { "ai feedback response generateFeedback: ${response.response}" }
        val feedback = Feedback(aiFeedback = response.response, user = user, sentence = sentence)

        return feedback
    }

    fun generateChat(command: ChatCommand.Generate, user: User, chatRoom: ChatRoom, chatHistory: String, nextSequence: Int): ChatMessage {
        val aiRequest = AiChatCommand.AiRequest.ChatRequest(
            input = chatHistory,
            personaType = command.personaType
        )
        val response = generate(aiRequest)
        logger.info { "ai answer response generateChat: ${response.response}" }
        val chatMessage = command.toEntity(user, chatRoom, response.response, nextSequence)


        return chatMessage
    }

    fun greetingChat(personaType: PersonaType, user: User, chatRoom: ChatRoom, chatHistory: String, nextSequence: Int): ChatMessage {
        val aiRequest = AiChatCommand.AiRequest.ChatRequest(
            input = chatHistory,
            personaType = personaType,
        )
        val response = generateGreeting(aiRequest)
        logger.info { "ai answer response greetingChat: ${response.response}" }
        val chatMessage = ChatMessage(user = user, chatRoom = chatRoom, message = response.response, senderType = SenderType.AI, sequence = nextSequence)

        return chatMessage
    }

    fun phraseChat(
        personaType: PersonaType,
        user: User,
        chatRoom: ChatRoom,
        previousHistory: String,
        currentAnswer: String,
        nextSequence: Int
    ): ChatMessage {
        val aiRequest = AiChatCommand.AiRequest.PhraseRequest(
            history = previousHistory,
            currentAnswer = currentAnswer,
            personaType = personaType,
        )
        val response = generateParagraph(aiRequest)
        logger.info { "ai answer response phraseChat: ${response.response}" }

        return ChatMessage(user = user, chatRoom = chatRoom, message = response.response, senderType = SenderType.AI, sequence = nextSequence)
    }

    fun generateChatRoomName(command: ChatCommand.Register): AiChatCommand.ChatResponse {
        val aiRequest = AiChatCommand.AiRequest.ChatRoomNameRequest(
            input = command.message,
            personaType = PersonaType.COWORKER
        )
        val response = generate(aiRequest)
        logger.info { "ai answer response generateChatRoomName: ${response.response}" }

        return response
    }
}
