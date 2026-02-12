package com.learner.language.testutils.fixture

import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatRoom
import com.learner.language.domain.chat.SenderType
import com.learner.language.domain.prompt.PersonaType // Corrected import
import com.learner.language.domain.user.User
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime

object ChatFixture {

    fun createChatRoom(
        id: Long = 1L,
        user: User, // User is now a required parameter
        personaType: PersonaType = PersonaType.CHILD,
        name: String = "Test Chat Room",
        lastMessageDateTime: LocalDateTime = LocalDateTime.now()
    ): ChatRoom {
        return ChatRoom(
            user = user,
            personaType = personaType,
            name = name,
            lastMessageDateTime = lastMessageDateTime
        ).apply { setId(id) }
    }

    fun createChatMessage(
        id: Long = 1L,
        user: User, // Added
        chatRoom: ChatRoom, // ChatRoom is now a required parameter
        senderType: SenderType = SenderType.USER,
        message: String = "Test Message",
        sequence: Int = 1
        // Removed sendDateTime: LocalDateTime
    ): ChatMessage {
        return ChatMessage(
            user = user, // Added
            chatRoom = chatRoom,
            senderType = senderType,
            message = message,
            sequence = sequence
        ).apply { setId(id) }
    }

}

fun ChatRoom.setId(id: Long) {
    ReflectionTestUtils.setField(this, "id", id)
}

fun ChatMessage.setId(id: Long) {
    ReflectionTestUtils.setField(this, "id", id)
}