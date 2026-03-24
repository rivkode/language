package com.learner.language.application.chat

import com.learner.language.domain.audio.AudioSpeechInfo
import com.learner.language.domain.audio.AudioTranscribeInfo
import com.learner.language.domain.chat.*
import com.learner.language.domain.user.UserReader
import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ChatFacade(
    private val chatService: ChatService,
    private val userReader: UserReader,
    private val chatRoomReader: ChatRoomReader,
    private val chatReader: ChatReader,
    private val chatWriter: ChatWriter
) {
    fun hello(): String {
        return chatService.hello()
    }

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

    fun greetingChatV2(userId: Long, command: ChatCommand.Generate): ChatMessageInfo {
        val user = userReader.getUserById(userId)
        val chatRoom = chatRoomReader.getChatRoomById(command.chatRoomId)
        val chatMessageList = chatReader.getChatMessageListByChatRoomId(command.chatRoomId)
        val greetingChatMessage = chatService.createGreetingChatMessage(
            user = user,
            chatRoom = chatRoom,
            personaType = command.personaType,
            chatMessageList = chatMessageList
        )
        val savedChatMessage = chatWriter.save(greetingChatMessage)

        return ChatMessageInfo(savedChatMessage)
    }

    fun phraseChat(command: ChatCommand.Phrase): ChatMessageInfo {
        val user = userReader.getUserById(command.userId)
        val chatRoom = chatRoomReader.getChatRoomById(command.chatRoomId)
        val currentChatMessage = chatReader.getChatMessageById(command.chatId)

        if (currentChatMessage.chatRoom.id != command.chatRoomId) {
            throw BadRequestException(ErrorCode.BAD_REQUEST, "chatId 와 chatRoomId 가 일치하지 않습니다")
        }

        if (currentChatMessage.user.id != command.userId) {
            throw BadRequestException(ErrorCode.BAD_REQUEST, "chatId 와 userId 가 일치하지 않습니다")
        }

        val previousChatMessages = chatReader.getPreviousChatMessages(
            chatRoomId = command.chatRoomId,
            sequence = currentChatMessage.sequence,
            limit = 4
        )
        val phraseChatMessage = chatService.createPhraseChatMessage(
            user = user,
            chatRoom = chatRoom,
            currentChatMessage = currentChatMessage,
            previousChatMessages = previousChatMessages
        )
        val savedChatMessage = chatWriter.save(phraseChatMessage)

        return ChatMessageInfo(savedChatMessage)
    }

}
