package com.learner.language.interfaces.chat

import com.learner.language.domain.chat.*
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

    data class SpeechRequest(
        val speechText: String,
        val chatRoomId: Long
    ) {
        fun toCommand(): ChatCommand.Speech {
            return ChatCommand.Speech(
                speechText = speechText,
                chatRoomId = chatRoomId
            )
        }
    }

    data class RegisterResponse(
        val chatMessageInfo: ChatMessageInfo
    )

    data class ChatListResponse(
        val chatMessageListInfo: List<ChatMessageInfo>
    )

    data class TranscribeResponse(
        val transcribeInfo : AudioTranscribeInfo
    )

    data class SpeechResponse(
        val audioRecordInfo : AudioRecordInfo
    )
}