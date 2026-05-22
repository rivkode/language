package com.learner.language.interfaces.shorts

data class ShortsFeedItem(
    val shortId: Long,
    val youtubeVideoId: String,
    val sourceUrl: String,
    val userState: ShortsUserStatePayload,
)

data class ShortsUserStatePayload(
    val saved: Boolean,
)

data class ShortsPagingPayload(
    val nextCursor: String?,
    val hasNext: Boolean,
)
