package com.learner.language.domain.ai

class AiChatCommand {
    sealed class AiRequest(
        open val input: String
    ) {
        data class FeedbackRequest(
            override val input: String,
            val noun: String = "",
            val verb: String = "",
            val adj: String = ""
        ) : AiRequest(input)

        data class ChatRequest(
            override val input: String
        ) : AiRequest(input)

        data class ChatRoomNameRequest(
            override val input: String
        ): AiRequest(input)
    }

    data class ChatResponse(
        val response: String
    )
}