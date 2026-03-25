package com.learner.language.domain.profile

data class ProfileInfo(
    val userId: Long,
    val username: String,
    val displayName: String,
    val bio: String?,
    val profileImageUrl: String?,
    val savedClipCount: Long,
    val isMe: Boolean,
)

data class SavedClipFeedInfo(
    val items: List<SavedClipFeedItemInfo>,
    val nextCursor: String?,
    val hasNext: Boolean,
)

data class SavedClipFeedItemInfo(
    val savedClipId: Long,
    val savedAt: String,
    val clip: SavedClipSummaryInfo,
)

data class SavedClipSummaryInfo(
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
