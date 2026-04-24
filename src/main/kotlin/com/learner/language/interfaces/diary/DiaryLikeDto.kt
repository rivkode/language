package com.learner.language.interfaces.diary

import com.learner.language.application.diary.DiaryLikeFacade
import jakarta.validation.constraints.NotNull

object DiaryLikeDto {

    data class ToggleRequest(
        @field:NotNull
        val liked: Boolean,
    )

    data class ToggleResponse(
        val diaryId: Long,
        val likeCount: Int,
        val userLiked: Boolean,
    ) {
        companion object {
            fun from(result: DiaryLikeFacade.Result) = ToggleResponse(
                diaryId = result.diaryId,
                likeCount = result.likeCount,
                userLiked = result.userLiked,
            )
        }
    }
}
