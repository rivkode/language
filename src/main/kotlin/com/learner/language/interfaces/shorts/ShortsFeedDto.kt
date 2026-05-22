package com.learner.language.interfaces.shorts

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

class ShortsFeedDto {
    data class FeedRequest(
        val cursor: String?,
        @field:Min(1, message = "size must be at least 1")
        @field:Max(50, message = "size must be at most 50")
        val size: Int = 10,
    )

    data class FeedResponse(
        val items: List<ShortsFeedItem>,
        val paging: ShortsPagingPayload,
    )
}
