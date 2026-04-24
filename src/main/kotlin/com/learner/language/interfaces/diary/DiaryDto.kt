package com.learner.language.interfaces.diary

import com.learner.language.application.diary.DiaryAuthorView
import com.learner.language.application.diary.DiaryCreateCommand
import com.learner.language.application.diary.DiaryView
import com.learner.language.common.pagination.CursorPage
import com.learner.language.common.pagination.PageInfo
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

object DiaryDto {

    data class CreateRequest(
        @field:NotNull
        @field:Size(min = 3, max = 3, message = "lines는 정확히 3개여야 합니다.")
        val lines: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val isPublic: Boolean = true,
    ) {
        fun toCommand(authorUserId: Long): DiaryCreateCommand = DiaryCreateCommand(
            authorUserId = authorUserId,
            lines = lines,
            tags = tags,
            isPublic = isPublic,
        )
    }

    data class AuthorResponse(
        val userId: Long,
        val username: String,
        val avatarUrl: String?,
    ) {
        companion object {
            fun from(view: DiaryAuthorView) = AuthorResponse(
                userId = view.userId,
                username = view.username,
                avatarUrl = view.avatarUrl,
            )
        }
    }

    data class DiaryResponse(
        val diaryId: Long,
        val author: AuthorResponse,
        val lines: List<String>,
        val createdAt: Instant,
        val tags: List<String>,
        val likeCount: Int,
        val commentCount: Int,
        val voiceParticipantCount: Int,
        val userLiked: Boolean,
        val isPublic: Boolean,
        val accentTone: String,
    ) {
        companion object {
            fun from(view: DiaryView) = DiaryResponse(
                diaryId = view.diaryId,
                author = AuthorResponse.from(view.author),
                lines = view.lines,
                createdAt = view.createdAt,
                tags = view.tags,
                likeCount = view.likeCount,
                commentCount = view.commentCount,
                voiceParticipantCount = view.voiceParticipantCount,
                userLiked = view.userLiked,
                isPublic = view.isPublic,
                accentTone = view.accentTone,
            )
        }
    }

    data class FeedResponse(
        val items: List<DiaryResponse>,
        val paging: PageInfo,
    ) {
        companion object {
            fun from(page: CursorPage<DiaryView>) = FeedResponse(
                items = page.items.map { DiaryResponse.from(it) },
                paging = page.paging,
            )
        }
    }
}
