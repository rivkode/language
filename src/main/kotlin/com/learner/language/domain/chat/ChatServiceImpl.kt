package com.learner.language.domain.chat

import com.learner.language.domain.ai.AiAudioService
import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.event.ChatEvent
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.chat.AudioRecordRepository
import com.learner.language.infrastructure.chat.AudioTranscribeRepository
import com.learner.language.infrastructure.chat.ChatRoomRepository
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ChatServiceImpl(
    private val chatWriter: ChatWriter,
    private val chatReader: ChatReader,
    private val chatRoomRepository: ChatRoomRepository,
    private val userReader: UserReader,
    private val aiChatService: AiChatService,
    private val aiAudioService: AiAudioService,
    private val audioTranscribeRepository: AudioTranscribeRepository,
    private val audioRecordRepository: AudioRecordRepository
): ChatService {
    override fun saveChat(command: ChatCommand.Register, userId: Long): ChatMessageInfo {
        val user = userReader.getUserById(userId)
        val chatRoom: ChatRoom = if (command.chatRoomId == null) {
            val aiChatRoomNameResponse = aiChatService.generateChatRoomName(command)
            val aiChatRoomName = aiChatRoomNameResponse.response
            chatRoomRepository.save(ChatRoom(user=user, name = aiChatRoomName))
        } else {
            chatRoomRepository.findById(command.chatRoomId).orElseThrow()
        }

        val nextSequence = getNextSequence(chatRoom.id)
        val chatMessage = command.toEntity(
            user = user,
            chatRoom = chatRoom,
            message = command.message,
            sequence = nextSequence
        )
        val savedChatMessage = chatWriter.save(chatMessage)
        chatRoom.updateLastMessageDateTime()
        chatRoomRepository.save(chatRoom)
        val chatMessageInfo = ChatMessageInfo(savedChatMessage)

        return chatMessageInfo
    }

    override fun generateChat(command: ChatCommand.Generate, userId: Long): ChatMessageInfo {
        val user = userReader.getUserById(userId)
        val chatRoom = chatRoomRepository.findById(command.chatRoomId).orElseThrow()
        val chatMessageList = chatReader.getChatMessageListByChatRoomId(command.chatRoomId)
        val chatHistory = toHistory(chatMessageList)
        val nextSequence = getNextSequence(command.chatRoomId)
        val chatMessage = aiChatService.generateChat(command, user, chatRoom, chatHistory, nextSequence)
        val savedChatMessage = chatWriter.save(chatMessage)
        val chatMessageInfo = ChatMessageInfo(savedChatMessage)

        return chatMessageInfo
    }

    override fun getChatRoomList(userId: Long): List<ChatRoomInfo> {
        val chatRoomList = chatRoomRepository.findByUserIdAndLastMessageDateTimeDesc(userId)
        val chatRoomListInfo = ChatRoomInfo.from(chatRoomList)


        return chatRoomListInfo
    }

    override fun getChatList(userId: Long, chatRoomId: Long): List<ChatMessageInfo> {
        val chatList = chatReader.getChatMessageListByChatRoomId(chatRoomId)
        val chatListInfo = ChatMessageInfo.from(chatList)

        return chatListInfo
    }

    override fun eventProcess(event: ChatEvent) {

    }

    private fun toHistory(chatMessageList: List<ChatMessage>): String {
        var history = ""

        for (chatMessage in chatMessageList) {
            var chat = ""
            if (chatMessage.senderType == SenderType.USER) {
                chat += "USER: "
            } else if (chatMessage.senderType == SenderType.AI) {
                chat += "AI: "
            }
            chat += chatMessage.message
            chat += "\n"
            history += chat
        }

        return history
    }

    private fun getNextSequence(chatRoomId: Long): Int {
        val lastChatMessage = chatReader.getLastChatMessageByChatRoomId(chatRoomId)
        if (lastChatMessage == null) {
            return 1
        } else {
            return lastChatMessage.sequence + 1
        }
    }

    override fun transcribeAudio(userId: Long, audioFile: MultipartFile): AudioTranscribeInfo {
        val transcribeText = aiAudioService.transcribe(audioFile)
        val user = userReader.getUserById(userId)
        val savedAudioTranscribe = audioTranscribeRepository.save(AudioTranscribe(text = transcribeText, user = user))
        val audioTranscribeInfo = AudioTranscribeInfo(savedAudioTranscribe)

        return audioTranscribeInfo
    }

    override fun speechAudio(command: ChatCommand.Speech, userId: Long): AudioRecordInfo {
        val speechText = command.speechText
        val user = userReader.getUserById(userId)
        val speechAudioFilePath = aiAudioService.speechAudio(speechText, userId)
        val audioRecord =
            AudioRecord(speechText = speechText, filePath = speechAudioFilePath, user = user)
        val savedAudioRecord = audioRecordRepository.save(audioRecord)
        val audioRecordInfo = AudioRecordInfo(savedAudioRecord)

        return audioRecordInfo
    }

}
