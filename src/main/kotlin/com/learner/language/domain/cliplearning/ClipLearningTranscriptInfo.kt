package com.learner.language.domain.cliplearning

data class ClipLearningTranscriptInfo(
    val videoId: String,
    val languagePriority: List<String>,
    val count: Int,
    val items: List<ClipLearningTranscriptItemInfo>,
)

data class ClipLearningTranscriptItemInfo(
    val text: String,
    val start: Double,
    val duration: Double,
)
