package com.learner.language.infrastructure.cliplearning

interface ClipLearningFeedQueryRepository {
    fun findFeedRows(category: String?, cursor: String?, size: Int): List<ClipLearningFeedRow>
    fun findClipRowByClipId(clipId: Long): ClipLearningFeedRow?
}

data class ClipLearningFeedRow(
    val clipId: Long,
    val sourceVideoId: Long,
    val youtubeVideoId: String,
    val sourceUrl: String,
    val title: String,
    val category: String,
    val channelName: String,
    val clipStartMs: Long,
    val clipEndMs: Long,
    val clipDurationMs: Long,
    val primarySentence: String,
    val translation: String?,
    val explanationSummary: String?,
    val usageTip: String?,
    val thumbnailUrl: String?,
)
