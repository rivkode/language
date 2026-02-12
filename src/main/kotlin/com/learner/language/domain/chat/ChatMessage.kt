package com.learner.language.domain.chat

import com.learner.language.common.BaseEntity
import com.learner.language.domain.audio.AudioSpeech
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "chat_message")
class ChatMessage(

    @Column(name = "message", length = 3000, nullable = false)
    val message: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    val chatRoom: ChatRoom,

    @Enumerated(EnumType.STRING)
    val senderType: SenderType,

    @Column(name = "sequence")
    val sequence: Int

): BaseEntity()

enum class SenderType {
    USER {
        override fun toInfo(
            chatMessage: ChatMessage,
            audioRecordMap: Map<Long, AudioSpeech>
        ): ChatMessageInfo =
            ChatMessageInfo(chatMessage)
    },
    AI {
        override fun toInfo(
            chatMessage: ChatMessage,
            audioRecordMap: Map<Long, AudioSpeech>
        ): ChatMessageInfo {
            val audioRecord = audioRecordMap[chatMessage.id]
                ?: throw IllegalStateException("AI message without audio")
            return ChatMessageInfo.from(chatMessage, audioRecord)
        }
    };

    abstract fun toInfo(
        chatMessage: ChatMessage,
        audioRecordMap: Map<Long, AudioSpeech>
    ): ChatMessageInfo
}
