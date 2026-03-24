package com.learner.language.domain.chat

interface ChatRoomReader {
    fun getChatRoomById(chatRoomId: Long): ChatRoom
}
