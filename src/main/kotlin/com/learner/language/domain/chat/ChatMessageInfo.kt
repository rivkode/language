package com.learner.language.domain.chat

import com.learner.language.domain.audio.AudioSpeech
import com.learner.language.domain.audio.AudioSpeechInfo

data class ChatMessageInfo(
    val userId: Long,
    val chatMessageId: Long,
    val chatRoomId: Long,
    val message: String,
    val sequence: Int,
    val senderType: SenderType,
    val audioSpeechInfo: AudioSpeechInfo?,
) {
    constructor(chatMessage: ChatMessage): this(
        userId = chatMessage.user.id,
        chatMessageId = chatMessage.id,
        chatRoomId = chatMessage.chatRoom.id,
        message = chatMessage.message,
        sequence = chatMessage.sequence,
        senderType = chatMessage.senderType,
        audioSpeechInfo = null
    )

    companion object {
        fun from(chatMessageList: List<ChatMessage>): List<ChatMessageInfo> {
            return chatMessageList.map {
                ChatMessageInfo(it)
            }
        }

        fun from(
            chatMessageList: List<ChatMessage>,
            audioRecordMap: Map<Long, AudioSpeech>
        ): List<ChatMessageInfo> {
            return chatMessageList.map {
                it.senderType.toInfo(it, audioRecordMap)
            }
        }

        fun from(chatMessage: ChatMessage, audioSpeech: AudioSpeech): ChatMessageInfo {
            return ChatMessageInfo(
                userId = chatMessage.user.id,
                chatMessageId = chatMessage.id,
                chatRoomId = chatMessage.chatRoom.id,
                message = chatMessage.message,
                sequence = chatMessage.sequence,
                senderType = chatMessage.senderType,
                audioSpeechInfo = AudioSpeechInfo(audioSpeech)
            )
        }
    }
}
