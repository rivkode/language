package com.learner.language.domain.diarychat

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "diary_chat_participant")
class DiaryChatParticipant(
    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "is_host", nullable = false)
    var isHost: Boolean = false,

    @Column(name = "joined_at", nullable = false)
    var joinedAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity()
