package com.learner.language.infrastructure.shorts

interface ShortsFeedQueryRepository {
    fun findFeedRows(cursor: String?, size: Int): List<ShortsFeedRow>
}

data class ShortsFeedRow(
    val shortId: Long,
    val youtubeVideoId: String,
    val sourceUrl: String,
)
