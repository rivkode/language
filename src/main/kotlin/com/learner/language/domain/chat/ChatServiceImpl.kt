package com.learner.language.domain.chat

import com.learner.language.domain.ai.AiAudioService
import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.audio.AudioSpeech
import com.learner.language.domain.audio.AudioSpeechInfo
import com.learner.language.domain.audio.AudioTranscribe
import com.learner.language.domain.audio.AudioTranscribeInfo
import com.learner.language.domain.event.ChatEvent
import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.audio.AudioSpeechRepository
import com.learner.language.infrastructure.audio.AudioTranscribeRepository
import com.learner.language.infrastructure.chat.ChatAudioSpeechMatchRepository
import com.learner.language.infrastructure.chat.ChatRoomRepository
import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode
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
    private val audioSpeechRepository: AudioSpeechRepository,
    private val chatAudioSpeechMatchRepository: ChatAudioSpeechMatchRepository,
): ChatService {
    override fun hello(): String {
        return "hello"
    }

    override fun createGreetingChatMessage(
        user: User,
        chatRoom: ChatRoom,
        personaType: PersonaType,
        chatMessageList: List<ChatMessage>
    ): ChatMessage {
        val chatHistory = toHistory(chatMessageList)
        val nextSequence = getNextSequence(chatMessageList)

        return aiChatService.greetingChat(personaType, user, chatRoom, chatHistory, nextSequence)
    }

    override fun createPhraseChatMessage(
        user: User,
        chatRoom: ChatRoom,
        currentChatMessage: ChatMessage,
        previousChatMessages: List<ChatMessage>
    ): ChatMessage {
        if (currentChatMessage.senderType != SenderType.USER) {
            throw BadRequestException(ErrorCode.BAD_REQUEST, "phrase 대상 메시지는 USER 이어야 합니다")
        }

        val previousHistory = toHistory(previousChatMessages)
        val nextSequence = getNextSequence(chatRoom.id)

        return aiChatService.phraseChat(
            personaType = chatRoom.personaType,
            user = user,
            chatRoom = chatRoom,
            previousHistory = previousHistory,
            currentAnswer = currentChatMessage.message,
            nextSequence = nextSequence
        )
    }

    override fun saveChat(command: ChatCommand.Register, userId: Long): ChatMessageInfo {
        val user = userReader.getUserById(userId)
        val chatRoom: ChatRoom = if (command.chatRoomId == null) {
            val aiChatRoomNameResponse = aiChatService.generateChatRoomName(command)
            val aiChatRoomName = aiChatRoomNameResponse.response
            chatRoomRepository.save(ChatRoom(user=user, name = aiChatRoomName, PersonaType.CHILD))
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
        val chatIds = chatList.map { it.id }
        val audioSpeechList =
            if (chatIds.isEmpty()) {
                emptyList()
            } else {
                audioSpeechRepository.findAllByChatIds(chatIds)
            }
        val chatAudioSpeechMatchList = chatAudioSpeechMatchRepository.findAllByChatIds(chatList.map { it.id })
        val audioSpeechMapById =
            audioSpeechList.associateBy { it.id }

        val chatIdToAudioSpeechMap =
            chatAudioSpeechMatchList
                .mapNotNull { match ->
                    audioSpeechMapById[match.audioSpeechId]?.let {
                        match.chatId to it
                    }
                }
                .toMap()

        val chatListInfo = ChatMessageInfo.from(chatList, chatIdToAudioSpeechMap)

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

    private fun getNextSequence(chatMessageList: List<ChatMessage>): Int {
        val lastChatMessage = chatMessageList.maxByOrNull { it.sequence }

        return if (lastChatMessage == null) {
            1
        } else {
            lastChatMessage.sequence + 1
        }
    }

    /*
    1. 사용자가 발화한 오디오를 입력받는다
    2. 입력받은 오디오를 transcribe 를 통해 텍스트로 변환한다
    3. 변환한 텍스트를 현재의 채팅방을 찾아서 올바르게 저장한다.
     */
    override fun transcribeAudio(userId: Long, chatRoomId: Long, audioFile: MultipartFile): AudioTranscribeInfo {
        val transcribeText = aiAudioService.transcribe(audioFile)
        val user = userReader.getUserById(userId)
        val command = ChatCommand.Register(chatRoomId, transcribeText)
//        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow()

        val chatRoom: ChatRoom = if (command.chatRoomId == null) {
            val aiChatRoomNameResponse = aiChatService.generateChatRoomName(command)
            val aiChatRoomName = aiChatRoomNameResponse.response
            chatRoomRepository.save(ChatRoom(user=user, name = aiChatRoomName, personaType = PersonaType.CHILD))
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

        val savedAudioTranscribe = audioTranscribeRepository.save(AudioTranscribe(text = transcribeText, user = user))
        val audioTranscribeInfo = AudioTranscribeInfo(savedAudioTranscribe)

        return audioTranscribeInfo
    }

    override fun speechAudio(command: ChatCommand.Speech, userId: Long): AudioSpeechInfo {
        val speechText = command.speechText
        val user = userReader.getUserById(userId)
        val speechAudioFilePath = aiAudioService.speechAudio(speechText, userId)
        val audioSpeech =
            AudioSpeech(text = speechText, filePath = speechAudioFilePath, user = user)
        val savedAudioRecord = audioSpeechRepository.save(audioSpeech)
        chatAudioSpeechMatchRepository.save(ChatAudioSpeechMatch(command.chatId, savedAudioRecord.id))
        val audioSpeechInfo = AudioSpeechInfo(savedAudioRecord)

        return audioSpeechInfo
    }

    override fun saveChatRoom(
        userId: Long,
        command: ChatRoomCommand.Register
    ): ChatRoomInfo {
        val user = userReader.getUserById(userId)
        val chatRoom = command.toEntity(user)
        val savedChatRoom = chatRoomRepository.save(chatRoom)

        return ChatRoomInfo(chatRoom = savedChatRoom)
    }

    /*
    1. 페르소나별로 응답 생성이 달라져야 한다
    2. 모든 응답은 음성 입력 출력으로 이루어져야 한다
    3. 채팅방 빠른 조회를 위해 userId, personaType 컬럼으로 인덱스를 생성한다 O
    4. 자연스러운 흐름을 위해 프런트에서 텍스트를 먼저 생성하고 이후에 음성을 알려준다
    5. 텍스트 생성시 물흐르듯 효과를 사용해서 생성한다
     */
    override fun greetingChat(
        userId: Long,
        command: ChatCommand.Generate
    ): ChatMessageInfo {
        val user = userReader.getUserById(userId)
        val chatRoomId = command.chatRoomId
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow()
        val chatMessageList = chatReader.getChatMessageListByChatRoomId(chatRoomId)
        val chatHistory = toHistory(chatMessageList)
        val nextSequence = getNextSequence(chatRoomId)
        val chatMessage = aiChatService.greetingChat(command.personaType, user, chatRoom, chatHistory, nextSequence)
        val savedChatMessage = chatWriter.save(chatMessage)
        val chatMessageInfo = ChatMessageInfo(savedChatMessage)

        return chatMessageInfo
    }
}
