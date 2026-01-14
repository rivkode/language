package com.learner.language.interfaces.ai

import jakarta.validation.constraints.NotEmpty

class AiChatDto {
    sealed class AiRequest(
        @NotEmpty(message = "User Input is missing.")
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
