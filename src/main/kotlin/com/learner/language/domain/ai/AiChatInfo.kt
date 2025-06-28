package com.learner.language.domain.ai

class AiChatInfo {
    sealed class AiRequest(
        open val userInput: String
    ) {
        data class AiFeedbackRequest(
            override val userInput: String,
            val noun: String = "",
            val verb: String = "",
            val adj: String = ""
        ) : AiRequest(userInput)

        data class AiChatRequest(
            override val userInput: String
        ) : AiRequest(userInput)
    }

    data class AiChatResponse(
        val response: String
    )
}