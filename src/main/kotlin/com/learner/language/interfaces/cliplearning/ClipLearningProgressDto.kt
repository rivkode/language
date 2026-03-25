package com.learner.language.interfaces.cliplearning

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

class ClipLearningProgressDto {
    data class ProgressRequest(
        @field:NotNull(message = "clipId is required")
        @field:Positive(message = "clipId must be positive")
        val clipId: Long?,
        @field:NotNull(message = "lastViewedPositionMs is required")
        @field:Min(0, message = "lastViewedPositionMs must be 0 or greater")
        val lastViewedPositionMs: Long?,
        @field:NotNull(message = "repeatEnabled is required")
        val repeatEnabled: Boolean?,
        @field:NotNull(message = "translationVisible is required")
        val translationVisible: Boolean?,
        @field:NotNull(message = "completed is required")
        val completed: Boolean?,
    )

    data class ProgressResponse(
        val clipId: Long,
        val updatedAt: String,
    )
}
