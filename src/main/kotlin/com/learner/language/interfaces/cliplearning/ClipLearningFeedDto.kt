package com.learner.language.interfaces.cliplearning

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

class ClipLearningFeedDto {
    data class FeedRequest(
        val cursor: String?,
        @field:Min(1, message = "size must be at least 1")
        @field:Max(50, message = "size must be at most 50")
        val size: Int = 10,
        val category: String?,
    )

    data class FeedResponse(
        val items: List<ClipLearningClipPageItem>,
        val paging: ClipLearningPagingPayload,
    )
}
