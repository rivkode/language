package com.learner.language.interfaces.cliplearning

data class ClipLearningClipPageItem(
    val clipId: Long,
    val youtubeVideoId: String,
    val clipStartMs: Long,
    val clipEndMs: Long,
    val title: String,
    val userState: ClipLearningUserStatePayload,
)

data class ClipLearningUserStatePayload(
    val saved: Boolean,
)

data class ClipLearningPagingPayload(
    val nextCursor: String?,
    val hasNext: Boolean,
)
