package com.learner.language.application.diary

import java.time.Instant

data class DiaryView(
    val diaryId: Long,
    val author: DiaryAuthorView,
    val lines: List<String>,
    val createdAt: Instant,
    val tags: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val voiceParticipantCount: Int,
    val userLiked: Boolean,
    val isPublic: Boolean,
    val accentTone: String,
)

data class DiaryAuthorView(
    val userId: Long,
    val username: String,
    val avatarUrl: String?,
)
