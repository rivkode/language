package com.learner.language.interfaces.cliplearning

import jakarta.validation.constraints.NotNull

class ClipLearningSaveDto {
    data class SaveRequest(
        @field:NotNull(message = "saved is required")
        val saved: Boolean?,
    )

    data class SaveResponse(
        val clipId: Long,
        val saved: Boolean,
        val savedAt: String?,
    )
}
