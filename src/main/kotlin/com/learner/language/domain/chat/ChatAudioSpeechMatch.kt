package com.learner.language.domain.chat

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "chat_audio_speech_match")
class ChatAudioSpeechMatch(
    @Column(name = "chat_id", nullable = false)
    val chatId: Long,

    @Column(name = "audio_speech_id")
    val audioSpeechId: Long,

): BaseEntity()
