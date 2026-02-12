package com.learner.language.domain.audio

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "audio_transcribe")
class AudioTranscribe(
    @Column(name = "text", length = 3000, nullable = false)
    val text: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,


): BaseEntity()