package com.learner.language.domain.chat

import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.User

class ChatRoomCommand {
    data class Register(
        val personaType: PersonaType,
        val contextType: ChatContextType = ChatContextType.GENERAL,
        val youtubeVideoId: String? = null,
        val name: String? = null,
    ) {
        fun toEntity(user: User): ChatRoom {
            return ChatRoom(
                user = user,
                name = name ?: defaultRoomName(),
                personaType = personaType,
                contextType = contextType,
                videoId = youtubeVideoId,
            )
        }

        private fun defaultRoomName(): String {
            return when (contextType) {
                ChatContextType.GENERAL -> "새 대화"
                ChatContextType.VIDEO_TRANSCRIPT -> "영상 대화 ${youtubeVideoId.orEmpty()}".trim()
            }
        }
    }
}
