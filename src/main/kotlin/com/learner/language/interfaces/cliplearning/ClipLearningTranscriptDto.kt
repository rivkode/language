package com.learner.language.interfaces.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningTranscriptInfo
import jakarta.validation.constraints.NotBlank

class ClipLearningTranscriptDto {
    data class TranscriptRequest(
        @field:NotBlank(message = "youtubeVideoId must not be blank")
        val youtubeVideoId: String,
    )

    data class TranscriptResponse(
        val youtubeVideoId: String,
        val languagePriority: List<String>,
        val count: Int,
        val items: List<TranscriptItemResponse>,
    ) {
        constructor(info: ClipLearningTranscriptInfo) : this(
            youtubeVideoId = info.videoId,
            languagePriority = info.languagePriority,
            count = info.count,
            items = info.items.map {
                TranscriptItemResponse(
                    text = it.text,
                    start = it.start,
                    duration = it.duration
                )
            }
        )
    }

    data class TranscriptItemResponse(
        val text: String,
        val start: Double,
        val duration: Double,
    )
}
