package com.learner.language.interfaces.chat

import com.learner.language.domain.chat.ChatRoomCommand
import com.learner.language.domain.chat.ChatRoomInfo
import com.learner.language.domain.prompt.PersonaType
import jakarta.validation.constraints.NotEmpty

class ChatRoomDto {
    data class RegisterRequest(
        @NotEmpty(message = "personaType is empty")
        val personaType: PersonaType,
    ) {
        fun toCommand(): ChatRoomCommand.Register {
            return ChatRoomCommand.Register(
                personaType = personaType
            )
        }
    }

    data class RegisterResponse(
        val chatRoomInfo: ChatRoomInfo
    )

    data class Response(
        val chatRoomListInfo: List<ChatRoomInfo>
    )

}
