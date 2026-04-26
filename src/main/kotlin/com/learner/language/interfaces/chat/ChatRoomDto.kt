package com.learner.language.interfaces.chat

import com.learner.language.domain.chat.ChatRoomCommand
import com.learner.language.domain.chat.ChatContextType
import com.learner.language.domain.chat.ChatRoomInfo
import com.learner.language.domain.prompt.PersonaType
import jakarta.validation.constraints.NotEmpty

class ChatRoomDto {
    data class RegisterRequest(
        val personaType: PersonaType,
        val contextType: ChatContextType = ChatContextType.GENERAL,
        val youtubeVideoId: String? = null,
        val name: String? = null,
    ) {
        fun toCommand(): ChatRoomCommand.Register {
            return ChatRoomCommand.Register(
                personaType = personaType,
                contextType = contextType,
                youtubeVideoId = youtubeVideoId,
                name = name
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
