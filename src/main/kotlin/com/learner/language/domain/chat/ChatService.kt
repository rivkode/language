package com.learner.language.domain.chat

import com.learner.language.domain.event.ChatEvent

interface ChatService {
    fun saveChat(command: ChatCommand.Register, userId: Long): ChatMessageInfo
    fun generateChat(command: ChatCommand.Generate, userId: Long): ChatMessageInfo
    fun getChatRoomList(userId: Long): List<ChatRoomInfo>
    fun getChatList(userId: Long, chatRoomId: Long): List<ChatMessageInfo>
    fun eventProcess(event: ChatEvent)
}