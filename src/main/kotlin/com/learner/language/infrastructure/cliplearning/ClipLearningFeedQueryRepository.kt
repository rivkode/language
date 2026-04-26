package com.learner.language.infrastructure.cliplearning

interface ClipLearningFeedQueryRepository {
    fun findFeedRows(category: String?, cursor: String?, size: Int): List<ClipLearningFeedRow>
    fun findClipRowByClipId(clipId: Long): ClipLearningFeedRow?
}

data class ClipLearningFeedRow(
    val clipId: Long,
    val youtubeVideoId: String,
    val title: String,
    val clipStartMs: Long,
    val clipEndMs: Long,
)
