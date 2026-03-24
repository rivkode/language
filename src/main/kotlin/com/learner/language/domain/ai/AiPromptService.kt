package com.learner.language.domain.ai

import com.learner.language.domain.prompt.PersonaType
import com.learner.language.infrastructure.prompt.PromptRepository
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
    @Value("classpath:prompts/system-chat-message-0112-gpt.st") // gpt
    private val systemChatResource: Resource,
    @Value("classpath:prompts/system-phrase-message.st")
    private val systemPhraseResource: Resource,
    @Value("classpath:prompts/system-chatroomname-message.st")
    private val systemChatRoomNameResource: Resource,
    @Value("classpath:prompts/user-feedback-message-0515.st")
    private val userFeedbackResource: Resource,
    @Value("classpath:prompts/user-chat-message.st")
    private val userChatResource: Resource,
    @Value("classpath:prompts/user-phrase-message.st")
    private val userPhraseResource: Resource,
    @Value("classpath:prompts/user-chatroomname-message.st")
    private val userChatRoomNameResource: Resource,
    private val promptRepository: PromptRepository,
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

    private fun createUserPhraseMessage(history: String, currentAnswer: String): Message {
        return PromptTemplate(userPhraseResource).createMessage(
            mapOf(
                "history" to history,
                "current_answer" to currentAnswer,
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

    private fun createSystemChatMessage(prompt: String): Message =
        SystemPromptTemplate(prompt).createMessage(
        )

    private fun createSystemPhraseMessage(personaType: PersonaType): Message =
        SystemPromptTemplate(systemPhraseResource).createMessage(
            mapOf(
                "persona" to toKoreanPersona(personaType)
            )
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
                val prompt = promptRepository.findByPersonaType(aiChatRequest.personaType.type).prompt
                Prompt(
                    listOf(
                        createUserChatMessage(
                            aiChatRequest.input
                        ),
                        createSystemChatMessage(prompt)
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

            is AiChatCommand.AiRequest.PhraseRequest -> {
                Prompt(
                    listOf(
                        createUserPhraseMessage(
                            aiChatRequest.history,
                            aiChatRequest.currentAnswer
                        ),
                        createSystemPhraseMessage(aiChatRequest.personaType)
                    )
                )
            }
        }
    }

    private fun toKoreanPersona(personaType: PersonaType): String {
        return when (personaType) {
            PersonaType.CHILD -> "아이처럼 친근하고 다정한 말투"
            PersonaType.FRIEND -> "가까운 친구처럼 편안한 말투"
            PersonaType.COWORKER -> "동료처럼 자연스럽고 예의 있는 말투"
            PersonaType.TEACHER -> "선생님처럼 차분하고 배려 있는 말투"
        }
    }
}
