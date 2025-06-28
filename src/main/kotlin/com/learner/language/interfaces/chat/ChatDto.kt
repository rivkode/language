package com.learner.language.interfaces.chat

import com.learner.language.domain.chat.ChatCommand
import com.learner.language.domain.chat.ChatMessageInfo
import com.learner.language.domain.chat.SenderType
import jakarta.validation.constraints.NotEmpty

class ChatDto {
    data class RegisterRequest(
        val chatRoomId: Long?,
        @NotEmpty(message = "userQuestion is empty")
        val message: String,
    ) {
        fun toCommand(): ChatCommand.Register {
            return ChatCommand.Register(
                chatRoomId = chatRoomId,
                message = message
            )
        }
    }

    data class GenerateRequest(
        val chatRoomId: Long
    ) {
        fun toCommand(): ChatCommand.Generate {
            return ChatCommand.Generate(
                chatRoomId = chatRoomId,
            )
        }
    }

    data class RegisterResponse(
        val chatMessageInfo: ChatMessageInfo
    )

    data class ChatListResponse(
        val chatMessageListInfo: List<ChatMessageInfo>
    )
}