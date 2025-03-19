package com.learner.language.domain.ai

import com.learner.language.interfaces.ai.AiChatDto
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class AiPromptService(
    @Value("classpath:prompts/system-feedback-message.st")
    private val systemFeedbackResource: Resource,
    @Value("classpath:prompts/system-question-message.st")
    private val systemQuestionResource: Resource,
    @Value("classpath:prompts/user-feedback-message.st")
    private val userFeedbackResource: Resource,
    @Value("classpath:prompts/user-question-message.st")
    private val userQuestionResource: Resource,
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

    private fun createUserQuestionMessage(userInput: String): Message {
        return PromptTemplate(userQuestionResource).createMessage(
            mapOf(
                "user_question" to userInput,
            )
        )
    }


    private fun createSystemFeedbackMessage(): Message =
        SystemPromptTemplate(systemFeedbackResource).createMessage(
        )

    private fun createSystemQuestionMessage(): Message =
        SystemPromptTemplate(systemQuestionResource).createMessage(
        )

    fun createPrompt(aiChatRequest: AiChatDto.AiRequest): Prompt {
        return when (aiChatRequest) {
            is AiChatDto.AiRequest.AiFeedbackRequest -> {
                Prompt(
                    listOf(
                        createUserFeedbackMessage(
                            aiChatRequest.userInput,
                            aiChatRequest.noun,
                            aiChatRequest.verb,
                            aiChatRequest.adj
                        ),
                        createSystemFeedbackMessage()
                    )
                )
            }

            is AiChatDto.AiRequest.AiAnswerRequest -> {
                Prompt(
                    listOf(
                        createUserQuestionMessage(
                            aiChatRequest.userInput
                        ),
                        createSystemQuestionMessage()
                    )
                )
            }
        }
    }
}
