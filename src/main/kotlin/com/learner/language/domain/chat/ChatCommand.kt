package com.learner.language.domain.chat

import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.User

class ChatCommand {
    data class Register(
        val chatRoomId: Long?,
        val message: String,
    ) {
        fun toEntity(user: User, chatRoom: ChatRoom, message: String, sequence: Int): ChatMessage {
            return ChatMessage(
                user = user,
                chatRoom = chatRoom,
                message = message,
                senderType = SenderType.USER,
                sequence = sequence
            )
        }
    }

    data class Generate(
        val chatRoomId: Long,
        val personaType: PersonaType
    ) {
        fun toEntity(user: User, chatRoom: ChatRoom, message: String, sequence: Int): ChatMessage {
            return ChatMessage(
                user = user,
                chatRoom = chatRoom,
                message = message,
                senderType = SenderType.AI,
                sequence = sequence
            )
        }
    }

    data class Speech(
        val speechText: String,
        val chatId: Long,
    ) {
    }
}