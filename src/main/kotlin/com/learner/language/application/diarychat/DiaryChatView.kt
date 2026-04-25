package com.learner.language.application.diarychat

import com.learner.language.application.diary.DiaryAuthorView
import java.time.Instant

data class DiaryChatRoomView(
    val roomId: Long,
    val diaryId: Long,
    val hostUserId: Long,
    val aiAssistantEnabled: Boolean,
    val participantCount: Int,
    val createdAt: Instant,
)

data class DiaryChatParticipantView(
    val user: DiaryAuthorView,
    val isHost: Boolean,
    val joinedAt: Instant,
)

data class DiaryChatMessageView(
    val messageId: Long,
    val roomId: Long,
    val author: DiaryAuthorView,
    val text: String,
    val audioUrl: String?,
    val createdAt: Instant,
    val source: String,
)

data class DiaryChatEventView(
    val type: String,
    val userId: Long?,
    val enabled: Boolean?,
    val at: Instant,
)

data class DiaryChatPollView(
    val items: List<DiaryChatMessageView>,
    val events: List<DiaryChatEventView>,
    val nextAfter: Long,
)
