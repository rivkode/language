package com.learner.language.interfaces.diarychat

import com.learner.language.application.diarychat.DiaryChatEventView
import com.learner.language.application.diarychat.DiaryChatMessageView
import com.learner.language.application.diarychat.DiaryChatParticipantView
import com.learner.language.application.diarychat.DiaryChatPollView
import com.learner.language.application.diarychat.DiaryChatRoomView
import com.learner.language.interfaces.diary.DiaryDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.Instant

object DiaryChatDto {

    data class CreateChatRoomRequest(
        @field:Positive val diaryId: Long,
        val aiAssistantEnabled: Boolean = true,
    )

    data class AiToggleRequest(
        @field:NotNull val enabled: Boolean,
    )

    data class SendMessageRequest(
        @field:NotBlank val text: String,
        val audioUrl: String? = null,
    )

    data class ChatRoomResponse(
        val roomId: Long,
        val diaryId: Long,
        val hostUserId: Long,
        val aiAssistantEnabled: Boolean,
        val participantCount: Int,
        val createdAt: Instant,
    ) {
        companion object {
            fun from(view: DiaryChatRoomView) = ChatRoomResponse(
                roomId = view.roomId,
                diaryId = view.diaryId,
                hostUserId = view.hostUserId,
                aiAssistantEnabled = view.aiAssistantEnabled,
                participantCount = view.participantCount,
                createdAt = view.createdAt,
            )
        }
    }

    data class ParticipantResponse(
        val user: DiaryDto.AuthorResponse,
        val isHost: Boolean,
        val joinedAt: Instant,
    ) {
        companion object {
            fun from(view: DiaryChatParticipantView) = ParticipantResponse(
                user = DiaryDto.AuthorResponse.from(view.user),
                isHost = view.isHost,
                joinedAt = view.joinedAt,
            )
        }
    }

    data class ParticipantListResponse(
        val items: List<ParticipantResponse>,
    )

    data class ChatMessageResponse(
        val messageId: Long,
        val roomId: Long,
        val author: DiaryDto.AuthorResponse,
        val text: String,
        val audioUrl: String?,
        val createdAt: Instant,
        val source: String,
    ) {
        companion object {
            fun from(view: DiaryChatMessageView) = ChatMessageResponse(
                messageId = view.messageId,
                roomId = view.roomId,
                author = DiaryDto.AuthorResponse.from(view.author),
                text = view.text,
                audioUrl = view.audioUrl,
                createdAt = view.createdAt,
                source = view.source,
            )
        }
    }

    data class MessageHistoryResponse(
        val items: List<ChatMessageResponse>,
        val hasMore: Boolean,
        val oldestMessageId: Long?,
    )

    data class ChatEventResponse(
        val type: String,
        val userId: Long?,
        val enabled: Boolean?,
        val at: Instant,
    ) {
        companion object {
            fun from(view: DiaryChatEventView) = ChatEventResponse(
                type = view.type,
                userId = view.userId,
                enabled = view.enabled,
                at = view.at,
            )
        }
    }

    data class PollResponse(
        val items: List<ChatMessageResponse>,
        val events: List<ChatEventResponse>,
        val nextAfter: Long,
    ) {
        companion object {
            fun from(view: DiaryChatPollView) = PollResponse(
                items = view.items.map { ChatMessageResponse.from(it) },
                events = view.events.map { ChatEventResponse.from(it) },
                nextAfter = view.nextAfter,
            )
        }
    }
}
