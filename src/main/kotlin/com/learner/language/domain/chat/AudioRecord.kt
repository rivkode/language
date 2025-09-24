package com.learner.language.domain.chat

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "audio_record")
class AudioRecord(
    @Column(name = "text", length = 3000, nullable = false)
    val speechText: String,

    @Column(name = "file_path", nullable = false)
    val filePath: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity() {

}