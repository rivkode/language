package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatRoom
import com.learner.language.domain.chat.ChatRoomReader
import org.springframework.stereotype.Component

@Component
class ChatRoomReaderImpl(
    private val chatRoomRepository: ChatRoomRepository
) : ChatRoomReader {
    override fun getChatRoomById(chatRoomId: Long): ChatRoom {
        return chatRoomRepository.findById(chatRoomId).orElseThrow()
    }
}
