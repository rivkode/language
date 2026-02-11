package com.learner.language.interfaces.chat

import com.learner.language.domain.audio.AudioSpeechInfo
import com.learner.language.domain.audio.AudioTranscribeInfo
import com.learner.language.domain.chat.*
import com.learner.language.domain.prompt.PersonaType
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
        val chatRoomId: Long,
        val personaType: PersonaType,
    ) {
        fun toCommand(): ChatCommand.Generate {
            return ChatCommand.Generate(
                chatRoomId = chatRoomId,
                personaType = personaType
            )
        }
    }

    data class SpeechRequest(
        val speechText: String,
        val chatId: Long
    ) {
        fun toCommand(): ChatCommand.Speech {
            return ChatCommand.Speech(
                speechText = speechText,
                chatId = chatId
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
        val audioSpeechInfo : AudioSpeechInfo
    )
}