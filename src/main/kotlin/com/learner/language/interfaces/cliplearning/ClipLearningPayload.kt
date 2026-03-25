package com.learner.language.interfaces.cliplearning

data class ClipLearningClipPageItem(
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
)

data class ClipLearningExplanationPayload(
    val summary: String,
    val grammarPoints: List<String>,
    val vocabulary: List<ClipLearningVocabularyItemPayload>,
    val usageTip: String,
)

data class ClipLearningVocabularyItemPayload(
    val word: String,
    val meaning: String,
)

data class ClipLearningUserStatePayload(
    val saved: Boolean,
    val completed: Boolean,
    val masteryLevel: String,
    val lastViewedAt: String?,
    val repeatCount: Int,
    val shadowingCount: Int,
)

data class ClipLearningPagingPayload(
    val nextCursor: String?,
    val hasNext: Boolean,
)
