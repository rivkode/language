package com.learner.language.domain.chat

import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.User

class ChatRoomCommand {
    data class Register(
        val personaType: PersonaType,
    ) {
        fun toEntity(user: User): ChatRoom {
            return ChatRoom(
                user = user,
                personaType = personaType,
            )
        }
    }
}
