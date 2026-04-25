package com.learner.language.domain.diarychat

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "diary_chat_room")
class DiaryChatRoom(
    @Column(name = "diary_id", nullable = false, unique = true)
    var diaryId: Long,

    @Column(name = "host_user_id", nullable = false)
    var hostUserId: Long,

    @Column(name = "ai_assistant_enabled", nullable = false)
    var aiAssistantEnabled: Boolean = true,

    @Column(name = "participant_count", nullable = false)
    var participantCount: Int = 0,

    @Column(name = "last_activity_at", nullable = false)
    var lastActivityAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity() {

    fun isHostedBy(userId: Long): Boolean = this.hostUserId == userId

    fun updateAiAssistant(enabled: Boolean) {
        this.aiAssistantEnabled = enabled
        this.lastActivityAt = LocalDateTime.now()
    }

    fun touchActivity() {
        this.lastActivityAt = LocalDateTime.now()
    }

    companion object {
        const val PARTICIPANT_LIMIT = 20
        const val MESSAGE_MAX_LENGTH = 2000
    }
}
