package com.learner.language.domain.ai

import com.learner.language.domain.prompt.PersonaType
import org.springframework.ai.chat.prompt.Prompt

class AiChatCommand {
    sealed class AiRequest(
        open val input: String,
        open val personaType: PersonaType
    ) {
        abstract fun createPrompt(
            aiPromptService: AiPromptService
        ): Prompt

        data class FeedbackRequest(
            override val input: String,
            override val personaType: PersonaType,
            val noun: String = "",
            val verb: String = "",
            val adj: String = ""
        ) : AiRequest(input, personaType) {
            override fun createPrompt(aiPromptService: AiPromptService): Prompt {
                return aiPromptService.createPrompt(this)
            }
        }

        data class ChatRequest(
            override val input: String,
            override val personaType: PersonaType,
        ) : AiRequest(input, personaType) {
            override fun createPrompt(aiPromptService: AiPromptService): Prompt {
                return aiPromptService.createPrompt(this)
            }
        }

        data class TranscriptChatRequest(
            val history: String,
            val transcriptContext: String,
            override val personaType: PersonaType,
            override val input: String = history,
        ) : AiRequest(input, personaType) {
            override fun createPrompt(aiPromptService: AiPromptService): Prompt {
                return aiPromptService.createPrompt(this)
            }
        }

        data class ChatRoomNameRequest(
            override val input: String,
            override val personaType: PersonaType,
        ): AiRequest(input, personaType) {
            override fun createPrompt(aiPromptService: AiPromptService): Prompt {
                return aiPromptService.createPrompt(this)
            }
        }

        data class PhraseRequest(
            val history: String,
            val currentAnswer: String,
            override val personaType: PersonaType,
            override val input: String = currentAnswer,
        ) : AiRequest(input, personaType) {
            override fun createPrompt(aiPromptService: AiPromptService): Prompt {
                return aiPromptService.createPrompt(this)
            }
        }
    }

    data class ChatResponse(
        val response: String
    )
}
