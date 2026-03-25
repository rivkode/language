package com.learner.language.interfaces.cliplearning

class ClipLearningClipDto {
    data class ClipDetailResponse(
        val clipId: Long,
        val sourceVideoId: Long,
        val youtubeVideoId: String,
        val sourceUrl: String,
        val title: String,
        val category: String,
        val channelName: String,
        val clipStartMs: Long,
        val clipEndMs: Long,
        val primarySentence: String,
        val userState: ClipLearningUserStatePayload,
        val thumbnailUrl: String?,
        val translation: String?,
        val explanation: ClipLearningExplanationPayload?,
        val clipDurationMs: Long?,
    ) {
        constructor(item: ClipLearningClipPageItem) : this(
            clipId = item.clipId,
            sourceVideoId = item.sourceVideoId,
            youtubeVideoId = item.youtubeVideoId,
            sourceUrl = item.sourceUrl,
            title = item.title,
            category = item.category,
            channelName = item.channelName,
            clipStartMs = item.clipStartMs,
            clipEndMs = item.clipEndMs,
            primarySentence = item.primarySentence,
            userState = item.userState,
            thumbnailUrl = item.thumbnailUrl,
            translation = item.translation,
            explanation = item.explanation,
            clipDurationMs = item.clipDurationMs,
        )
    }
}
