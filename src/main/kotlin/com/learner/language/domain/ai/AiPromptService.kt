package com.learner.language.domain.ai

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AiPromptService(
    @Value("classpath:prompts/system-feedback-message-0515.st")
    private val systemFeedbackResource: Resource,
    @Value("classpath:prompts/system-chat-message.st")
    private val systemChatResource: Resource,
    @Value("classpath:prompts/system-chatroomname-message.st")
    private val systemChatRoomNameResource: Resource,
    @Value("classpath:prompts/user-feedback-message-0515.st")
    private val userFeedbackResource: Resource,
    @Value("classpath:prompts/user-chat-message.st")
    private val userChatResource: Resource,
    @Value("classpath:prompts/user-chatroomname-message.st")
    private val userChatRoomNameResource: Resource,
) {
    private fun createUserFeedbackMessage(userInput: String, noun: String, verb: String, adj: String): Message {
        return PromptTemplate(userFeedbackResource).createMessage(
            mapOf(
                "user_sentence" to userInput,
                "noun" to noun,
                "verb" to verb,
                "adj" to adj
            )
        )
    }

    private fun createUserChatMessage(history: String): Message {
        return PromptTemplate(userChatResource).createMessage(
            mapOf(
                "history" to history,
            )
        )
    }

    private fun createUserChatRoomNameMessage(message: String): Message {
        return PromptTemplate(userChatRoomNameResource).createMessage(
            mapOf(
                "message" to message,
            )
        )
    }


    private fun createSystemFeedbackMessage(): Message =
        SystemPromptTemplate(systemFeedbackResource).createMessage(
        )

    private fun createSystemChatMessage(): Message =
        SystemPromptTemplate(systemChatResource).createMessage(
        )

    private fun createSystemChatRoomNameMessage(): Message =
        SystemPromptTemplate(systemChatRoomNameResource).createMessage(
        )



    fun createPrompt(aiChatRequest: AiChatCommand.AiRequest): Prompt {
        return when (aiChatRequest) {
            is AiChatCommand.AiRequest.FeedbackRequest -> {
                Prompt(
                    listOf(
                        createUserFeedbackMessage(
                            aiChatRequest.input,
                            aiChatRequest.noun,
                            aiChatRequest.verb,
                            aiChatRequest.adj
                        ),
                        createSystemFeedbackMessage()
                    )
                )
            }

            is AiChatCommand.AiRequest.ChatRequest -> {
                Prompt(
                    listOf(
                        createUserChatMessage(
                            aiChatRequest.input
                        ),
                        createSystemChatMessage()
                    )
                )
            }

            is AiChatCommand.AiRequest.ChatRoomNameRequest -> {
                Prompt(
                    listOf(
                        createUserChatRoomNameMessage(
                            aiChatRequest.input
                        ),
                        createSystemChatRoomNameMessage()
                    )
                )
            }
        }
    }
}
