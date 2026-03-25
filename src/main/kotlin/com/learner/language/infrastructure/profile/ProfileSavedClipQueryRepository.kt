package com.learner.language.infrastructure.profile

import java.time.LocalDateTime

interface ProfileSavedClipQueryRepository {
    fun findSavedClipRows(userId: Long, cursor: String?, size: Int, category: String?): List<ProfileSavedClipRow>
}

data class ProfileSavedClipRow(
    val savedClipId: Long,
    val savedAt: LocalDateTime,
    val clipId: Long,
    val sourceVideoId: Long,
    val youtubeVideoId: String,
    val sourceUrl: String,
    val thumbnailUrl: String?,
    val title: String,
    val category: String,
    val channelName: String,
    val clipStartMs: Long,
    val clipEndMs: Long,
    val clipDurationMs: Long?,
    val primarySentence: String,
)
