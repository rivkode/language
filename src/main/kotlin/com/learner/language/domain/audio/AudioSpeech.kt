package com.learner.language.domain.audio

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "audio_speech")
class AudioSpeech(
    @Column(name = "text", length = 3000, nullable = false)
    val text: String,

    @Column(name = "file_path", nullable = false)
    val filePath: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity() {

}