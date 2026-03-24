package com.learner.language.domain.chat

interface ChatReader {
    fun getChatMessageListByChatRoomId(chatRoomId: Long): List<ChatMessage>
    fun getLastChatMessageByChatRoomId(chatRoomId: Long): ChatMessage?
    fun getChatMessageById(chatId: Long): ChatMessage
    fun getPreviousChatMessages(chatRoomId: Long, sequence: Int, limit: Int): List<ChatMessage>
}
