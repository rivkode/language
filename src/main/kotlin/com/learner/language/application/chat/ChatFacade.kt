package com.learner.language.application.chat

import com.learner.language.domain.chat.ChatCommand
import com.learner.language.domain.chat.ChatMessageInfo
import com.learner.language.domain.chat.ChatRoomInfo
import com.learner.language.domain.chat.ChatService
import org.springframework.stereotype.Service

@Service
class ChatFacade(
    private val chatService: ChatService
) {
    fun registerChat(command: ChatCommand.Register, userId: Long): ChatMessageInfo {
        val chatInfo = chatService.saveChat(command, userId)

        return chatInfo
    }

    fun generateChat(command: ChatCommand.Generate, userId: Long): ChatMessageInfo {
        val chatInfo = chatService.generateChat(command, userId)

        return chatInfo
    }

    fun retrieveChat(userId: Long, chatRoomId: Long): List<ChatMessageInfo> {
        val chatListInfo = chatService.getChatList(userId, chatRoomId)

        return chatListInfo
    }

    fun retrieveChatRoom(userId: Long): List<ChatRoomInfo> {
        val chatRoomListInfo = chatService.getChatRoomList(userId)

        return chatRoomListInfo
    }

}