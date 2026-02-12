package com.learner.language.application.chat

import com.learner.language.domain.audio.AudioSpeechInfo
import com.learner.language.domain.audio.AudioTranscribeInfo
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

    fun registerChatRoom(userId: Long, command: ChatRoomCommand.Register): ChatRoomInfo {
        val chatRoomInfo = chatService.saveChatRoom(userId, command)

        return chatRoomInfo
    }

    fun transcribeAudio(userId: Long, chatRoomId: Long, audioFile: MultipartFile): AudioTranscribeInfo {
        val audioTranscribeInfo = chatService.transcribeAudio(userId, chatRoomId, audioFile)

        return audioTranscribeInfo
    }

    fun speechAudio(command: ChatCommand.Speech, userId: Long): AudioSpeechInfo {
        val speechInfo = chatService.speechAudio(command, userId)

        return speechInfo
    }

    fun greetingChat(userId: Long, command: ChatCommand.Generate): ChatMessageInfo {
        val chatInfo = chatService.greetingChat(userId, command)

        return chatInfo
    }

}