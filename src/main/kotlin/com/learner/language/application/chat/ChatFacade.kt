package com.learner.language.application.chat

import com.learner.language.domain.chat.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

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

    fun transcribeAudio(userId: Long, audioFile: MultipartFile): AudioTranscribeInfo  {
        val audioTranscribeInfo = chatService.transcribeAudio(userId, audioFile)

        return audioTranscribeInfo
    }

    fun speechAudio(command: ChatCommand.Speech, userId: Long): AudioRecordInfo {
        val speechInfo = chatService.speechAudio(command, userId)

        return speechInfo
    }

}