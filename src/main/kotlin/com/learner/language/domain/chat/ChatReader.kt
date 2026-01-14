package com.learner.language.domain.chat

interface ChatReader {
    fun getChatMessageListByChatRoomId(chatRoomId: Long): List<ChatMessage>
    fun getLastChatMessageByChatRoomId(chatRoomId: Long): ChatMessage?
}